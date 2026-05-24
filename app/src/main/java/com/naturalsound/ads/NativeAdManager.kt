package com.naturalsound.ads

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import uz.apprica.naturalsound.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages loading and lifecycle of a single [NativeAd].
 *
 * Usage:
 * ```
 * val manager = NativeAdManager(context)
 * manager.loadAd()
 *
 * // Observe in Compose:
 * val ad by manager.nativeAd.collectAsStateWithLifecycle()
 * ad?.let { NativeAdCard(it) }
 *
 * // Release when done (e.g. onDestroy):
 * manager.destroyAd()
 * ```
 */
class NativeAdManager(private val context: Context) {

    private val _nativeAd = MutableStateFlow<NativeAd?>(null)

    /** The currently loaded ad, or `null` while loading / after destruction. */
    val nativeAd: StateFlow<NativeAd?> = _nativeAd.asStateFlow()

    private val _isLoading = MutableStateFlow(false)

    /** `true` while an ad request is in flight. */
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Starts a new ad request. No-ops if a request is already in flight.
     * Destroys the previous ad before storing the new one.
     */
    fun loadAd() {
        if (_isLoading.value) return
        _isLoading.value = true

        val adLoader = AdLoader.Builder(context, AD_UNIT_ID)
            .forNativeAd { ad ->
                // Destroy the old ad to prevent memory leaks before replacing it.
                _nativeAd.value?.destroy()
                _nativeAd.value = ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    _isLoading.value = false
                    Log.d(TAG, "Native ad loaded successfully.")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    _isLoading.value = false
                    Log.e(TAG, "Native ad failed to load — code=${error.code} msg=${error.message}")
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    /** Destroys the currently held ad and clears the state. Call this in `onDestroy`. */
    fun destroyAd() {
        _nativeAd.value?.destroy()
        _nativeAd.value = null
    }

    companion object {
        private const val TAG = "NativeAdManager"

        //dev
//        const val AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"

        //prod
        const val AD_UNIT_ID = "ca-app-pub-6710003016622154/8592415850"
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// View-binding helper
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Wires all [NativeAd] assets into the inflated [NativeAdView] (R.layout.ad_native).
 * Handles optional fields by hiding views when the asset is unavailable.
 */
fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
    // Register asset views with the SDK (required for click tracking).
    adView.mediaView = adView.findViewById<MediaView>(R.id.media_view)
    adView.headlineView = adView.findViewById(R.id.tv_headline)
    adView.bodyView = adView.findViewById(R.id.tv_body)
    adView.callToActionView = adView.findViewById(R.id.btn_cta)
    adView.iconView = adView.findViewById(R.id.iv_icon)
    adView.advertiserView = adView.findViewById(R.id.tv_advertiser)

    // Headline (always present).
    (adView.headlineView as TextView).text = nativeAd.headline

    // Media content.
    nativeAd.mediaContent?.let { adView.mediaView?.mediaContent = it }

    // Body (optional).
    if (nativeAd.body == null) {
        adView.bodyView?.visibility = View.INVISIBLE
    } else {
        adView.bodyView?.visibility = View.VISIBLE
        (adView.bodyView as TextView).text = nativeAd.body
    }

    // Call-to-action (optional).
    if (nativeAd.callToAction == null) {
        adView.callToActionView?.visibility = View.INVISIBLE
    } else {
        adView.callToActionView?.visibility = View.VISIBLE
        (adView.callToActionView as Button).text = nativeAd.callToAction
    }

    // App icon (optional).
    if (nativeAd.icon == null) {
        adView.iconView?.visibility = View.GONE
    } else {
        adView.iconView?.visibility = View.VISIBLE
        (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
    }

    // Advertiser name (optional).
    if (nativeAd.advertiser == null) {
        adView.advertiserView?.visibility = View.INVISIBLE
    } else {
        adView.advertiserView?.visibility = View.VISIBLE
        (adView.advertiserView as TextView).text = nativeAd.advertiser
    }

    // Commit — must be called last.
    adView.setNativeAd(nativeAd)
}

// ─────────────────────────────────────────────────────────────────────────────
// Composable
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Composable wrapper around [R.layout.ad_native].
 *
 * Inflates the XML layout once via [AndroidView] and re-populates it whenever
 * [nativeAd] changes, so the Compose host doesn't need to manage the View lifecycle.
 *
 * Example usage inside a LazyColumn:
 * ```
 * val ad by nativeAdManager.nativeAd.collectAsStateWithLifecycle()
 * ad?.let {
 *     item { NativeAdCard(nativeAd = it, modifier = Modifier.fillMaxWidth()) }
 * }
 * ```
 */
@Composable
fun NativeAdCard(
    nativeAd: NativeAd,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            // FrameLayout dummy parent: layout params aniqlanadi, lekin unga attach bo'lmaydi.
            val parent = FrameLayout(ctx)
            val adView = LayoutInflater.from(ctx)
                .inflate(R.layout.ad_native, parent, false) as NativeAdView
            populateNativeAdView(nativeAd, adView)
            adView
        },
        update = { adView ->
            populateNativeAdView(nativeAd, adView)
        }
    )
}
