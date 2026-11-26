package com.bibaiboba.app.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.Settings
import android.widget.Toast

data class DeviceAction(
    val type: String,
    val payload: String
)

object DeviceActionExecutor {

    private val actionRegex = Regex("\\[ACTION:([A-Z_]+):(.*?)]\\s*$", RegexOption.DOT_MATCHES_ALL)

    fun extractAction(text: String): Pair<String, DeviceAction?> {
        val match = actionRegex.find(text.trim()) ?: return text to null
        val type = match.groupValues[1].trim().uppercase()
        val payload = match.groupValues[2].trim()
        val cleanText = text.removeRange(match.range).trim()
        return cleanText to DeviceAction(type, payload)
    }

    fun execute(context: Context, action: DeviceAction): Result<String> {
        return runCatching {
            when (action.type) {
                "OPEN_URL" -> openUrl(context, action.payload)
                "OPEN_APP" -> openApp(context, action.payload)
                "CALL" -> call(context, action.payload, direct = true)
                "DIAL" -> call(context, action.payload, direct = false)
                "SEARCH" -> search(context, action.payload)
                "MAPS" -> maps(context, action.payload)
                "ALARM" -> alarm(context, action.payload)
                "TIMER" -> timer(context, action.payload)
                "SETTINGS" -> openSettings(context, action.payload)
                "SHARE" -> share(context, action.payload)
                "COPY" -> copy(context, action.payload)
                else -> "Неизвестное действие: ${action.type}"
            }
        }
    }

    private fun openUrl(context: Context, rawUrl: String): String {
        val url = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) rawUrl else "https://$rawUrl"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return "Открываю ссылку: $url"
    }

    private fun openApp(context: Context, target: String): String {
        val pm = context.packageManager
        val trimmed = target.trim()
        // Try as package name first
        val byPackage = pm.getLaunchIntentForPackage(trimmed)
        if (byPackage != null) {
            byPackage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(byPackage)
            return "Открываю приложение: $trimmed"
        }
        // Search by label
        val launchIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = pm.queryIntentActivities(launchIntent, 0)
        val match = apps.firstOrNull {
            it.loadLabel(pm).toString().contains(trimmed, ignoreCase = true) ||
                    it.activityInfo.packageName.contains(trimmed, ignoreCase = true)
        }
        if (match != null) {
            val intent = pm.getLaunchIntentForPackage(match.activityInfo.packageName)
                ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ?: return "Не удалось запустить ${match.loadLabel(pm)}"
            context.startActivity(intent)
            val label = match.loadLabel(pm).toString()
            return "Открываю приложение: $label"
        }
        // Fallback: search in Play Store / web
        val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=${Uri.encode(trimmed)}&c=apps"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return runCatching {
            context.startActivity(searchIntent)
            "Приложение \"$trimmed\" не найдено, открываю поиск в магазине"
        }.getOrElse {
            openUrl(context, "https://play.google.com/store/search?q=${Uri.encode(trimmed)}&c=apps")
        }
    }

    private fun call(context: Context, number: String, direct: Boolean): String {
        val clean = number.trim()
        val action = if (direct) Intent.ACTION_DIAL else Intent.ACTION_DIAL // use DIAL for safety without runtime permission handling complexity
        val intent = Intent(action, Uri.parse("tel:${Uri.encode(clean)}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return if (direct) "Открываю звонок: $clean" else "Открываю набор номера: $clean"
    }

    private fun search(context: Context, query: String): String {
        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra("query", query)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return runCatching {
            context.startActivity(intent)
            "Ищу: $query"
        }.getOrElse {
            openUrl(context, "https://www.google.com/search?q=${Uri.encode(query)}")
        }
    }

    private fun maps(context: Context, query: String): String {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(query)}"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return runCatching {
            context.startActivity(intent)
            "Открываю карты: $query"
        }.getOrElse {
            openUrl(context, "https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")
        }
    }

    private fun alarm(context: Context, payload: String): String {
        val parts = payload.split(":")
        val hour = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: return "Не удалось распознать время будильника: $payload"
        val minute = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 0
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            putExtra(AlarmClock.EXTRA_MESSAGE, "Будильник от Bibaiboba")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return "Ставлю будильник на %02d:%02d".format(hour, minute)
    }

    private fun timer(context: Context, payload: String): String {
        val seconds = payload.filter { it.isDigit() }.toIntOrNull() ?: return "Не удалось распознать длительность таймера: $payload"
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, seconds)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            putExtra(AlarmClock.EXTRA_MESSAGE, "Таймер от Bibaiboba")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return "Ставлю таймер на $seconds сек."
    }

    private fun openSettings(context: Context, payload: String): String {
        val action = when (payload.trim().lowercase()) {
            "wifi" -> Settings.ACTION_WIFI_SETTINGS
            "bluetooth" -> Settings.ACTION_BLUETOOTH_SETTINGS
            "display" -> Settings.ACTION_DISPLAY_SETTINGS
            "sound" -> Settings.ACTION_SOUND_SETTINGS
            "apps" -> Settings.ACTION_APPLICATION_SETTINGS
            "accessibility" -> Settings.ACTION_ACCESSIBILITY_SETTINGS
            else -> Settings.ACTION_SETTINGS
        }
        context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "Открываю настройки: ${payload.ifBlank { "main" }}"
    }

    private fun share(context: Context, text: String): String {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Поделиться через").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "Открываю меню sharing"
    }

    private fun copy(context: Context, text: String): String {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Bibaiboba", text))
        Toast.makeText(context, "Скопировано", Toast.LENGTH_SHORT).show()
        return "Скопировано в буфер обмена"
    }
}
