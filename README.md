# Event Countdown App

An Android application that displays a **permanent ongoing countdown notification** in your system notification shade for an upcoming event or task in days (e.g. `"15 Days"` or `"15 days till Vacation"`).

---

## Download & Install APK

The easiest way to install the app on your Android phone is to download the APK from the GitHub Releases page:

**[Download Latest APK from Releases](../../releases/latest)**

1. Download `CountdownApp.apk` directly on your phone.
2. Tap the downloaded file to install. *(If prompted, allow installation from your browser/file manager).*

---

## Features

- **Target Date Selection**: Easily input an event name and pick a target date.
- **Permanent Ongoing Notification**: Keeps a persistent notification in your system notification shade counting down remaining days.
- **Adaptive Notification Layout**:
  - **Minimized State**: Displays only the days remaining (e.g., `15 Days`).
  - **Expanded State**: Reveals full details (e.g., `15 days till Vacation`).
- **In-App Text Color Customization**: Customize the text color of your countdown notification directly in the app (Default, White, Black, Red, Blue, Green, Orange, Purple).
- **Daily Background Updates**: Powered by Android **WorkManager** to automatically update remaining days at midnight.
- **Device Reboot Support**: Automatically restores active countdown notifications after device reboot (`BOOT_COMPLETED`).
- **Dismissal Protection**: Auto-restores notification if accidentally swiped away while the countdown is active.

---

## Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: Android Material Components, ConstraintLayout, ScrollView
- **Background Scheduling**: [Jetpack WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- **Local Persistence**: `SharedPreferences`
- **Notifications**: `NotificationCompat`, `NotificationManager`, Custom `RemoteViews`
- **Permissions**: Android 13+ (`POST_NOTIFICATIONS`), `RECEIVE_BOOT_COMPLETED`

---

## Getting Started

### Prerequisites
- **Android Studio**: Jellyfish / Koala or newer
- **JDK**: Java 11 or higher
- **Android Device / Emulator**: Running Android 7.0 (API 24) or higher

### Building & Running
1. **Clone the repository**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/CountdownApp.git
   cd CountdownApp
   ```
2. **Open in Android Studio**:
   - Open Android Studio and select **Open**.
   - Navigate to the `CountdownApp` directory.
3. **Build & Run**:
   - Connect your Android device or start an emulator.
   - Click **Run ▶** (or press `Shift + F10`).

---

## License

This project is open source and available under the [MIT License](LICENSE).

