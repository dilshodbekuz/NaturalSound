package com.naturalsound.ui.theme

import androidx.compose.runtime.compositionLocalOf
import com.naturalsound.domain.model.SoundCategory

data class AppStrings(
    // ── Navigation ────────────────────────────────────────────────────────────
    val navHome: String,
    val navMixer: String,
    val navTimer: String,
    val navProfile: String,

    // ── Onboarding ────────────────────────────────────────────────────────────
    val langSubtitle: String,
    val langButton: String,
    val nameTitle: String,
    val nameSubtitle: String,
    val nameButton: String,

    // ── Home ──────────────────────────────────────────────────────────────────
    val homeGreeting: String,
    val homePopular: String,
    val homeResults: (Int) -> String,
    val homeNotFound: (String) -> String,
    val homeEmpty: String,
    val homeLoading: String,
    val searchHint: String,
    val activeSounds: (Int) -> String,
    val soundMix: (Int) -> String,

    // ── Categories ────────────────────────────────────────────────────────────
    val catAll: String,
    val catRain: String,
    val catForest: String,
    val catOcean: String,
    val catFire: String,
    val catStorm: String,
    val catBirds: String,
    val catWind: String,
    val catSpace: String,

    // ── Timer ─────────────────────────────────────────────────────────────────
    val timerTitle: String,
    val timerSubtitle: String,
    val sleepTimer: String,
    val timeLeft: String,
    val fadeOutSubtitle: String,
    val fadeDuration: String,
    val seconds: (Int) -> String,
    val fadeOutNow: String,
    val wakeAlarm: String,
    val wakeAlarmSub: String,
    val statistics: String,
    val statToday: String,
    val statStreak: String,
    val statSessions: String,
    val timerStart: String,
    val timerStop: String,
    val add5Min: String,
    val add10Min: String,
    val alarmDialogTitle: String,
    val save: String,
    val cancel: String,
    val hourAbbr: String,
    val minAbbr: String,

    // ── Profile ───────────────────────────────────────────────────────────────
    val profilePremium: String,
    val sectionSettings: String,
    val wifiDownload: String,
    val wifiDownloadSub: String,
    val notifications: String,
    val notificationsSub: String,
    val nightMode: String,
    val nightModeSub: String,
    val sectionEq: String,
    val eqReset: String,
    val sectionOther: String,
    val firebaseSync: String,
    val firebaseSyncSub: String,
    val aboutApp: String,
    val aboutAppSub: String,
    val logout: String,
    val logoutSub: String,
    val language: String,
    val languageName: String,
) {
    fun categoryLabel(cat: SoundCategory) = when (cat) {
        SoundCategory.ALL    -> catAll
        SoundCategory.RAIN   -> catRain
        SoundCategory.FOREST -> catForest
        SoundCategory.OCEAN  -> catOcean
        SoundCategory.FIRE   -> catFire
        SoundCategory.STORM  -> catStorm
        SoundCategory.BIRDS  -> catBirds
        SoundCategory.WIND   -> catWind
        SoundCategory.SPACE  -> catSpace
    }
}

// ── O'zbek ────────────────────────────────────────────────────────────────────
val stringsUz = AppStrings(
    navHome = "Asosiy", navMixer = "Mixer", navTimer = "Taymer", navProfile = "Profil",
    langSubtitle = "Davom etish uchun tilni tanlang",
    langButton = "Davom etish  →",
    nameTitle = "Ismingizni kiriting",
    nameSubtitle = "Ilova sizni shu ism bilan chaqiradi",
    nameButton = "Boshlash  🚀",
    homeGreeting = "Xush kelibsiz,",
    homePopular = "Mashhur ovozlar",
    homeResults = { n -> "Natijalar ($n)" },
    homeNotFound = { q -> "«$q» topilmadi" },
    homeEmpty = "Ovozlar yo'q",
    homeLoading = "Yuklanmoqda...",
    searchHint = "Ovoz qidirish...",
    activeSounds = { n -> "$n ta ovoz" },
    soundMix = { n -> "$n ta ovoz aralashmasi" },
    catAll = "Hammasi", catRain = "Yomg'ir", catForest = "O'rmon",
    catOcean = "Oqean", catFire = "Olov", catStorm = "Bo'ron",
    catBirds = "Qushlar", catWind = "Shamol", catSpace = "Koinot",
    timerTitle = "Taymer", timerSubtitle = "Avtomatik boshqaruv",
    sleepTimer = "Uyqu taymeri", timeLeft = "qolgan vaqt",
    fadeOutSubtitle = "To'xtatishdan oldin ovoz sekin pasayadi",
    fadeDuration = "Davomiylik",
    seconds = { n -> "$n soniya" },
    fadeOutNow = "Hozir fade out bilan to'xtat",
    wakeAlarm = "Uyg'onish alarmi", wakeAlarmSub = "Tabiat ovozi bilan uyg'onish",
    statistics = "STATISTIKA", statToday = "Bugun", statStreak = "Kun seriya",
    statSessions = "Sessiyalar",
    timerStart = "Boshlash", timerStop = "To'xtatish",
    add5Min = "+5 daq", add10Min = "+10 daq",
    alarmDialogTitle = "Alarm vaqti", save = "Saqlash", cancel = "Bekor",
    hourAbbr = "s", minAbbr = "d",
    profilePremium = "Premium foydalanuvchi",
    sectionSettings = "SOZLAMALAR",
    wifiDownload = "Wi-Fi da avtomatik yuklash", wifiDownloadSub = "Yangi ovozlarni offline saqlash",
    notifications = "Xabarnomalar", notificationsSub = "Service va yangi ovozlar haqida",
    nightMode = "Tungi rejim", nightModeSub = "Ekran yorqinligini kamaytirish",
    sectionEq = "GLOBAL EQ", eqReset = "Standartga qaytarish",
    sectionOther = "BOSHQA",
    firebaseSync = "Firebase sinxronizatsiya", firebaseSyncSub = "Yangi ovozlar avtomatik yangilanadi",
    aboutApp = "Ilova haqida", aboutAppSub = "NaturalSound v1.0",
    logout = "Chiqish", logoutSub = "Hisobdan chiqish",
    language = "Til", languageName = "O'zbekcha",
)

// ── Ruscha ────────────────────────────────────────────────────────────────────
val stringsRu = AppStrings(
    navHome = "Главная", navMixer = "Микшер", navTimer = "Таймер", navProfile = "Профиль",
    langSubtitle = "Выберите язык для продолжения",
    langButton = "Продолжить  →",
    nameTitle = "Введите ваше имя",
    nameSubtitle = "Приложение будет называть вас так",
    nameButton = "Начать  🚀",
    homeGreeting = "Добро пожаловать,",
    homePopular = "Популярные звуки",
    homeResults = { n -> "Результаты ($n)" },
    homeNotFound = { q -> "«$q» не найдено" },
    homeEmpty = "Звуков нет",
    homeLoading = "Загрузка...",
    searchHint = "Поиск звуков...",
    activeSounds = { n -> "$n звука" },
    soundMix = { n -> "$n звуков в миксе" },
    catAll = "Все", catRain = "Дождь", catForest = "Лес",
    catOcean = "Океан", catFire = "Огонь", catStorm = "Гроза",
    catBirds = "Птицы", catWind = "Ветер", catSpace = "Космос",
    timerTitle = "Таймер", timerSubtitle = "Автоматическое управление",
    sleepTimer = "Таймер сна", timeLeft = "осталось",
    fadeOutSubtitle = "Звук плавно затихнет перед остановкой",
    fadeDuration = "Длительность",
    seconds = { n -> "$n сек" },
    fadeOutNow = "Остановить с затуханием",
    wakeAlarm = "Будильник", wakeAlarmSub = "Просыпайтесь под звуки природы",
    statistics = "СТАТИСТИКА", statToday = "Сегодня", statStreak = "Дней подряд",
    statSessions = "Сессии",
    timerStart = "Запустить", timerStop = "Остановить",
    add5Min = "+5 мин", add10Min = "+10 мин",
    alarmDialogTitle = "Время будильника", save = "Сохранить", cancel = "Отмена",
    hourAbbr = "ч", minAbbr = "мин",
    profilePremium = "Премиум пользователь",
    sectionSettings = "НАСТРОЙКИ",
    wifiDownload = "Авто-загрузка по Wi-Fi", wifiDownloadSub = "Сохранять новые звуки офлайн",
    notifications = "Уведомления", notificationsSub = "О сервисе и новых звуках",
    nightMode = "Ночной режим", nightModeSub = "Уменьшить яркость экрана",
    sectionEq = "GLOBAL EQ", eqReset = "Сбросить",
    sectionOther = "ПРОЧЕЕ",
    firebaseSync = "Синхронизация Firebase", firebaseSyncSub = "Автообновление звуков",
    aboutApp = "О приложении", aboutAppSub = "NaturalSound v1.0",
    logout = "Выйти", logoutSub = "Выйти из аккаунта",
    language = "Язык", languageName = "Русский",
)

// ── Inglizcha ─────────────────────────────────────────────────────────────────
val stringsEn = AppStrings(
    navHome = "Home", navMixer = "Mixer", navTimer = "Timer", navProfile = "Profile",
    langSubtitle = "Choose a language to continue",
    langButton = "Continue  →",
    nameTitle = "Enter your name",
    nameSubtitle = "The app will call you by this name",
    nameButton = "Get Started  🚀",
    homeGreeting = "Welcome,",
    homePopular = "Popular sounds",
    homeResults = { n -> "Results ($n)" },
    homeNotFound = { q -> "\"$q\" not found" },
    homeEmpty = "No sounds",
    homeLoading = "Loading...",
    searchHint = "Search sounds...",
    activeSounds = { n -> "$n sounds" },
    soundMix = { n -> "$n sound mix" },
    catAll = "All", catRain = "Rain", catForest = "Forest",
    catOcean = "Ocean", catFire = "Fire", catStorm = "Storm",
    catBirds = "Birds", catWind = "Wind", catSpace = "Space",
    timerTitle = "Timer", timerSubtitle = "Automatic control",
    sleepTimer = "Sleep timer", timeLeft = "remaining",
    fadeOutSubtitle = "Sound fades out before stopping",
    fadeDuration = "Duration",
    seconds = { n -> "$n sec" },
    fadeOutNow = "Stop with fade out now",
    wakeAlarm = "Wake alarm", wakeAlarmSub = "Wake up to nature sounds",
    statistics = "STATISTICS", statToday = "Today", statStreak = "Day streak",
    statSessions = "Sessions",
    timerStart = "Start", timerStop = "Stop",
    add5Min = "+5 min", add10Min = "+10 min",
    alarmDialogTitle = "Alarm time", save = "Save", cancel = "Cancel",
    hourAbbr = "h", minAbbr = "m",
    profilePremium = "Premium user",
    sectionSettings = "SETTINGS",
    wifiDownload = "Auto-download on Wi-Fi", wifiDownloadSub = "Save new sounds offline",
    notifications = "Notifications", notificationsSub = "About service and new sounds",
    nightMode = "Night mode", nightModeSub = "Reduce screen brightness",
    sectionEq = "GLOBAL EQ", eqReset = "Reset to default",
    sectionOther = "OTHER",
    firebaseSync = "Firebase sync", firebaseSyncSub = "New sounds auto-update",
    aboutApp = "About app", aboutAppSub = "NaturalSound v1.0",
    logout = "Log out", logoutSub = "Sign out of account",
    language = "Language", languageName = "English",
)

fun stringsFor(code: String): AppStrings = when (code) {
    "ru" -> stringsRu
    "en" -> stringsEn
    else -> stringsUz
}

val LocalStrings = compositionLocalOf<AppStrings> { stringsUz }
