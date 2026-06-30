# Calm Sounds — Google Play Release Guide

App: **Calm Sounds** · Package: `uz.apprica.naturalsound` · versionCode 1 / versionName 1.0

---

## 1. Signed AAB build qilish (Android Studio'da)

Keystore loyihada yo'q, shuning uchun signed bundle'ni Android Studio orqali yarating
(parolni faqat o'zingiz kiritasiz):

1. Android Studio'da loyihani oching.
2. Menyu: **Build → Generate Signed App Bundle / APK…**
3. **Android App Bundle** ni tanlang → Next.
4. **Key store path**: mavjud `.jks` faylingizni tanlang (yoki "Choose existing").
   Keystore parol, key alias, key parolni kiriting.
   - *Maslahat:* "Export encrypted key" ni belgilab, Play App Signing uchun zaxira oling.
5. **Build Variants**: `release` ni tanlang → Finish.
6. Tugagach AAB shu yerda bo'ladi:
   `app/build/outputs/bundle/release/app-release.aab`

**Muqobil (terminal orqali):** loyiha ildizida `keystore.properties` yarating:

```properties
storeFile=C:/path/to/your-keystore.jks
storePassword=********
keyAlias=your_alias
keyPassword=********
```

so'ng: `./gradlew bundleRelease` → AAB yuqoridagi joyda paydo bo'ladi.
> Eslatma: `keystore.properties` ni `.gitignore` ga qo'shing — git'ga tushmasin.

**Build'dan keyin sinab ko'ring:** `release` variantni o'zingizning qurilmangizga
o'rnatib, ovozlar yuklanishini va reklama chiqishini tekshiring (minify yoqilgan,
shuning uchun release build alohida sinaladi).

---

## 2. Privacy policy ni joylash (URL kerak)

`privacy-policy.html` tayyor (shu papkada). Play Console URL talab qiladi. Eng oson:

- **GitHub Pages:** yangi repo oching → `privacy-policy.html` ni `index.html` nomi bilan
  yuklang → Settings → Pages → Branch: main → Save. URL: `https://<username>.github.io/<repo>/`
- Yoki istalgan hosting (Netlify, Firebase Hosting, oddiy sayt).

URL'ni Play Console → **Policy → App content → Privacy policy** ga kiriting.

---

## 3. Store listing (Main store listing)

**App name:** Calm Sounds (30 belgigacha)

**Short description (80 belgigacha):**
> Relaxing nature sounds & ambient mixer for sleep, focus, and calm.

**Full description (English):**
> Calm Sounds helps you relax, focus, and fall asleep with high-quality nature and
> ambient sounds. Mix rain, ocean, forest, and more to create your perfect soundscape,
> set a sleep timer, and build a daily relaxation habit.
>
> Features:
> • A growing library of nature & ambient sounds (rain, forest, ocean, and more)
> • Sound mixer — blend multiple sounds and adjust each volume
> • Sleep timer to fade out automatically
> • Simple, calming dark interface
> • Track your daily listening streak
>
> Whether you need help sleeping, studying, meditating, or just unwinding, Calm Sounds
> gives you a peaceful space to breathe. Download now and find your calm.

**To'liq tavsif (O'zbekcha — qo'shimcha til sifatida qo'shsa bo'ladi):**
> Calm Sounds — uyqu, diqqat va xotirjamlik uchun tabiat va ambient ovozlari ilovasi.
> Yomg'ir, okean, o'rmon va boshqa ovozlarni aralashtirib, o'zingizga mos tovush muhitini
> yarating, uyqu taymerini o'rnating va har kuni dam olish odatini shakllantiring.
>
> Imkoniyatlar: tabiat ovozlari kutubxonasi · ovoz mikseri · uyqu taymeri · oddiy va
> tinch interfeys · kunlik tinglash seriyasi (streak).

---

## 4. Grafik materiallar (majburiy)

| Material | O'lcham | Izoh |
|---|---|---|
| App icon | 512×512 PNG | Shaffof emas, 32-bit |
| Feature graphic | 1024×500 PNG/JPG | Do'kon tepasidagi banner |
| Phone screenshots | kamida 2 ta (2–8) | min 320px, 16:9 yoki 9:16 |
| (ixtiyoriy) 7"/10" tablet skrinshotlar | — | tablet qo'llab-quvvatlansa |

> Skrinshotlarni emulyator yoki qurilmadan oling: Asosiy ekran, Mixer, Taymer, Profil.
> Icon sifatida `ic_launcher` dizaynidan 512×512 versiya eksport qiling.

---

## 5. Data safety formasi (Play Console javoblari)

App quyidagilarni **yig'adi/jo'natadi** (Firebase + AdMob orqali). Quyidagicha deklaratsiya qiling:

- **Does your app collect or share user data?** → **Yes**
- **Encryption in transit?** → Yes (HTTPS)
- **Can users request data deletion?** → Yes (email orqali) yoki kamida deletion siyosati ko'rsatilgan

Yig'iladigan ma'lumotlar (collected, not necessarily linked to identity):

| Data type | Collected | Purpose | Linked to user? |
|---|---|---|---|
| App interactions / usage | Yes (Firebase Analytics) | Analytics | No |
| Crash logs / diagnostics | Yes (Crashlytics) | App functionality, Analytics | No |
| Device or other IDs (Advertising ID) | Yes (AdMob) | Advertising / marketing | No |
| Approximate location (IP-based, ads) | Yes (AdMob) | Advertising | No |

> Ism, til, statistika **faqat qurilmada** saqlanadi → "collected" emas (jo'natilmaydi).

---

## 6. Content rating

App content rating savolnomasini to'ldiring. Calm Sounds uchun odatiy javoblar:
- Zo'ravonlik, jinsiy kontent, so'kinish, giyohvandlik → **Yo'q**
- Foydalanuvchilar o'zaro muloqot qiladimi? → Yo'q
- Joylashuv ulashiladimi? → Yo'q
- **Ads bormi?** → **Ha** (reklama mavjud)

Natija: ehtimol **Everyone / 3+**.

---

## 7. App content (Policy bo'limi) — to'ldiriladigan formalar

- **Privacy policy:** 2-bo'limdagi URL.
- **Ads:** "Yes, my app contains ads" → belgilang.
- **App access:** "All functionality is available without special access" (login yo'q, anonim).
- **Target audience & content:** yosh guruhini tanlang (masalan 13+); bolalarga
  mo'ljallanmagan.
- **Data safety:** 5-bo'lim.
- **Government apps / Financial features / Health:** Yo'q.

---

## 8. AAB yuklash va release (Play Console)

1. **All apps → Create app** → nomi "Calm Sounds", til, App/Game = App, Free.
2. Deklaratsiyalarni qabul qiling.
3. Avval **Testing → Internal testing** track'ga yuklab sinashni tavsiya qilaman
   (tezkor, review minimal), keyin **Production**.
4. **Create new release** → `app-release.aab` ni yuklang.
   - Birinchi marta **Play App Signing** ni qabul qiling (Google signing kalitni boshqaradi,
     siz upload key bilan yuklaysiz).
5. Release notes yozing (masalan: "Birinchi versiya").
6. Store listing, Content rating, Data safety, App content — barchasini yashil (✓) qiling.
7. **Review release → Start rollout to Production**.

> Birinchi review odatda bir necha soatdan bir necha kungacha davom etadi.

---

## Tez tekshiruv ro'yxati (checklist)

- [ ] Signed `app-release.aab` yaratildi
- [ ] Release build qurilmada sinaldi (ovoz + reklama ishlaydi)
- [ ] Privacy policy URL onlayn
- [ ] Icon 512×512, Feature 1024×500, ≥2 skrinshot
- [ ] Data safety to'ldirildi
- [ ] Content rating olindi
- [ ] Ads = Yes, App access, Target audience to'ldirildi
- [ ] AAB yuklandi → review'ga yuborildi
