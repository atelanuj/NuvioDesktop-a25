package com.nuvio.app.features.details

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.CancellationException
import com.nuvio.app.features.trailer.TrailerPlaybackSource
import kotlin.test.assertFailsWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HeroTrailerSelectorTest {

    @Test
    fun `selects official non-season YouTube trailer first`() {
        val trailers = listOf(
            trailer(id = "teaser", type = "Teaser", official = true, publishedAt = "2026-02-01"),
            trailer(id = "season", type = "Trailer", official = true, seasonNumber = 2, publishedAt = "2026-03-01"),
            trailer(id = "official", type = "Trailer", official = true, publishedAt = "2026-01-01"),
        )

        assertEquals("official", selectHeroTrailer(trailers)?.id)
    }

    @Test
    fun `falls back to addon-style YouTube trailer when official flag is absent`() {
        val trailers = listOf(
            trailer(id = "clip", type = "Clip", official = false, publishedAt = "2026-02-01"),
            trailer(id = "addon", type = "Trailer", official = false, publishedAt = "2026-01-01"),
        )

        assertEquals("addon", selectHeroTrailer(trailers)?.id)
    }

    @Test
    fun `uses season trailer when only season trailers are available`() {
        val trailers = listOf(
            trailer(id = "season-one", type = "Trailer", official = false, seasonNumber = 1),
            trailer(id = "season-two", type = "Trailer", official = true, seasonNumber = 2),
        )

        assertEquals("season-two", selectHeroTrailer(trailers)?.id)
    }

    @Test
    fun `filters out non YouTube trailers`() {
        val trailers = listOf(
            trailer(id = "vimeo", site = "Vimeo", type = "Trailer", official = true),
            trailer(id = "youtube", site = "YouTube", type = "Teaser", official = false),
        )

        assertEquals("youtube", selectHeroTrailer(trailers)?.id)
    }

    @Test
    fun `returns null for empty or unsupported trailer lists`() {
        assertNull(selectHeroTrailer(emptyList()))
        assertNull(selectHeroTrailer(listOf(trailer(id = "blank", key = ""))))
        assertNull(selectHeroTrailer(listOf(trailer(id = "other", site = "Vimeo"))))
    }

    @Test
    fun `tries alternate links after an unavailable official upload`() = runBlocking {
        val calls = mutableListOf<String>()
        val expected = TrailerPlaybackSource("https://example.com/trailer.mp4")
        val result = resolveHeroTrailerPlaybackSource(listOf(
            trailer(id = "official", official = true),
            trailer(id = "fallback"),
            trailer(id = "duplicate", key = "official"),
        )) { url ->
            calls += url
            if (url.endsWith("official")) throw IllegalStateException("Unavailable")
            expected
        }
        assertEquals(expected, result)
        assertEquals(listOf("https://www.youtube.com/watch?v=official", "https://www.youtube.com/watch?v=fallback"), calls)
    }

    @Test
    fun `cancellation stops fallback requests`() = runBlocking {
        var calls = 0
        assertFailsWith<CancellationException> {
            resolveHeroTrailerPlaybackSource(listOf(trailer(id = "one"), trailer(id = "two"))) {
                calls++
                throw CancellationException("Navigated away")
            }
        }
        assertEquals(1, calls)
    }

    private fun trailer(
        id: String,
        key: String = id,
        site: String = "YouTube",
        type: String = "Trailer",
        official: Boolean = false,
        publishedAt: String? = null,
        seasonNumber: Int? = null,
    ): MetaTrailer =
        MetaTrailer(
            id = id,
            key = key,
            name = id,
            site = site,
            type = type,
            official = official,
            publishedAt = publishedAt,
            seasonNumber = seasonNumber,
        )
}
