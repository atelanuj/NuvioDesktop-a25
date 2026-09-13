package com.nuvio.app.features.trailer

import kotlin.test.Test
import kotlin.test.assertEquals

class CombinedTrailerSourceTest {
    private fun manifest(height: Int, bitrate: Long = 4_000_000) =
        ManifestCandidate("test", 1, "https://example.com/master.m3u8", "https://example.com/variant.m3u8", height, bitrate)

    private fun progressive(height: Int, bitrate: Long = 1_000_000) = StreamCandidate(
        client = "test", priority = 1, url = "https://example.com/video.mp4",
        score = 0.0, bitrate = bitrate, mimeType = "video/mp4", hasN = false,
        height = height, fps = 30, ext = "mp4",
    )

    @Test
    fun higherResolutionHlsPrecedesLowResolutionCombinedVideo() {
        assertEquals(listOf("https://example.com/master.m3u8", "https://example.com/video.mp4"),
            rankCombinedTrailerSources(manifest(1080), progressive(360)))
    }

    @Test
    fun higherResolutionProgressiveStillWins() {
        assertEquals("https://example.com/video.mp4",
            rankCombinedTrailerSources(manifest(720), progressive(1080)).first())
    }

    @Test
    fun bitrateBreaksResolutionTiesAndMissingSourcesAreAllowed() {
        assertEquals("https://example.com/video.mp4",
            rankCombinedTrailerSources(manifest(720, 1_000_000), progressive(720, 3_000_000)).first())
        assertEquals(listOf("https://example.com/master.m3u8"), rankCombinedTrailerSources(manifest(1080), null))
        assertEquals(emptyList(), rankCombinedTrailerSources(null, null))
    }
}
