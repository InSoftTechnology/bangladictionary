# APKPure Distribution & Submission Guide
**Forhad Labs — Bangla Dictionary Release Pipeline**

This guide provides step-by-step instructions on compiling, signing, preparing marketing assets, and submitting your **Bangla Dictionary** mobile application to **APKPure Developer Console**.

---

## 1. Compile Signed Production Release Bundle

Compile your signed Android App Bundle (AAB) or release APK cleanly using local Gradle commands or the built-in GitHub Actions continuous delivery pipelines.

### Generating locally via Gradle
Set up environment variables representing your Keystore password and run:
```bash
gradle assembleRelease
```
This produces a production-ready, release-signed APK located inside:
`app/build/outputs/apk/release/app-release.apk`

---

## 2. APKPure Metadata Requirements

APKPure displays descriptive texts, cards, and categorized listings. Prepare and use the details compiled below for the submission page.

### App Identity Specs
*   **App Title:** Bangla Dictionary
*   **Developer Name:** Forhad Labs
*   **Category:** Books & Reference
*   **Primary Locale:** English / Bengali (BD)
*   **Price:** Free

### Short Description (Max 80 Characters)
> Fully offline Bangla to English / English to Bangla dictionary with Text-To-Speech.

### Full App Description (Short-listed for Storefront)
```markdown
Introducing the ultimate offline-first Bangla Dictionary, proudly engineered by Forhad Labs! Effortlessly search, translate, and pronounce thousands of English and Bangla words instantly without of any active internet access. Truly modern Material Design 3 layout featuring extensive A-Z word index explore slides, speech vocalizer, history, and administrative seed control tools.

★ CORE FEATURES:
- Dual Translations: Bangla to English and English to Bangla.
- Complete Offline Support: SQLite Room database indexes are saved directly on your Android phone.
- Text-to-Speech (TTS): Hear proper acoustic pronunciations of Bengali and English terms.
- Custom Bookmarks: Star and tag vocabularies to study later.
- Local Analytics: Gauge your progress with visual total word metrics and search frequencies.
- Word Approvals: Submit suggested additions directly to reviewers via guest panels.
- Advanced Bulk Importer: Developers can instantly paste JSON arrays or comma-separated CSV lists to reload the database.

Enjoy absolute premium book tracking, simple copypastas, and sharing flows. Completely ad-control optional with zero tracking scripts. Download today!
```

---

## 3. Custom Promotional Assets Checklist

To list on APKPure, upload the following visual design assets:

1.  **Application Icon Logo:**
    *   **Format:** Transparent PNG
    *   **Size:** `512 x 512` pixels
    *   **Aesthetic:** Center-weighted book logo (derived from your generated `bangla_dict_logo.jpg`)

2.  **Visual Screenshots (Minimum 4):**
    *   **Aspect Ratio:** Vertical `9:16` or `10:16`
    *   **Size:** `1080 x 1920` or `1200 x 2000` pixels
    *   **Screens to grab:**
        *   Screen 1: Search Tab showcasing a live translation search (e.g. typing "beautiful").
        *   Screen 2: Detail Card showing synonyms, example sentences, and TTS speak icon.
        *   Screen 3: A-Z Index Explorer slider filter.
        *   Screen 4: Developer Admin Seeder displaying bulk import and simulated remote switches.

3.  **Horizontal Feature Graphic Banner:**
    *   **Size:** `1024 x 500` pixels
    *   **Content:** Rich emerald green background with a gold styled academic logo and "Offline Bangla Dictionary — By Forhad Labs" display heading.

---

## 4. Privacy Page Link
APKPure mandates listing a valid privacy policy URL before certifying applications. 
We have generated a fully compliant, ready-made privacy sheet inside `/PRIVACY_POLICY.md` which you can upload or publish directly to Netlify, GitHub Pages, or Forhad Labs domain!
