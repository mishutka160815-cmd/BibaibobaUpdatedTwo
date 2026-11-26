package com.bibaiboba.app.util

object LocalCommandParser {

    fun parse(input: String): DeviceAction? {
        val text = input.trim().lowercase()
        if (text.isBlank()) return null

        // Timer: "таймер на 5 минут", "timer 30 seconds"
        Regex("""(?:таймер|timer)\s+(?:на\s+)?(\d+)\s*(сек|секунд|second|seconds|мин|минут|minute|minutes|час|часа|часов|hour|hours)?""")
            .find(text)?.let { m ->
                val n = m.groupValues[1].toIntOrNull() ?: return@let null
                val unit = m.groupValues[2]
                val seconds = when {
                    unit.startsWith("мин") || unit.startsWith("minute") -> n * 60
                    unit.startsWith("час") || unit.startsWith("hour") -> n * 3600
                    else -> n
                }
                return DeviceAction("TIMER", seconds.toString())
            }

        // Alarm: "будильник на 7:30", "alarm 07:30"
        Regex("""(?:будильник|alarm)\s+(?:на\s+)?(\d{1,2})[:.](\d{2})""")
            .find(text)?.let { m ->
                val h = m.groupValues[1]
                val min = m.groupValues[2]
                return DeviceAction("ALARM", "$h:$min")
            }

        // Open URL
        Regex("""(?:открой\s+сайт|open\s+url|открой\s+ссылку)\s+(.+)""", RegexOption.IGNORE_CASE)
            .find(input.trim())?.let { m ->
                return DeviceAction("OPEN_URL", m.groupValues[1].trim())
            }

        // Maps / navigation
        Regex("""(?:маршрут|как\s+добраться|навигаци\w*|maps?|где\s+находится)\s+(?:до\s+|к\s+|to\s+)?(.+)""", RegexOption.IGNORE_CASE)
            .find(input.trim())?.let { m ->
                return DeviceAction("MAPS", m.groupValues[1].trim())
            }

        // Search
        Regex("""(?:найди|поищи|загугли|search|google)\s+(.+)""", RegexOption.IGNORE_CASE)
            .find(input.trim())?.let { m ->
                return DeviceAction("SEARCH", m.groupValues[1].trim())
            }

        // Dial
        Regex("""(?:позвони|набери|dial|call)\s+([\+\d][\d\s\-\(\)]{4,})""", RegexOption.IGNORE_CASE)
            .find(input.trim())?.let { m ->
                return DeviceAction("DIAL", m.groupValues[1].trim())
            }

        // Settings
        if (text.contains("настройк")) {
            val payload = when {
                text.contains("wi-fi") || text.contains("wifi") || text.contains("вайф") -> "wifi"
                text.contains("bluetooth") || text.contains("блютуз") -> "bluetooth"
                text.contains("экран") || text.contains("диспле") || text.contains("display") -> "display"
                text.contains("звук") || text.contains("sound") -> "sound"
                text.contains("прилож") || text.contains("apps") -> "apps"
                text.contains("спец") || text.contains("accessibility") -> "accessibility"
                else -> "main"
            }
            return DeviceAction("SETTINGS", payload)
        }

        // Open app
        Regex("""(?:открой|запусти|launch|open)\s+(.+)""", RegexOption.IGNORE_CASE)
            .find(input.trim())?.let { m ->
                val target = m.groupValues[1].trim()
                if (target.isNotBlank() && target.length < 60) {
                    return DeviceAction("OPEN_APP", target)
                }
            }

        return null
    }
}
