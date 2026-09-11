package com.nuvio.app.features.details

internal fun selectHeroTrailer(trailers: List<MetaTrailer>): MetaTrailer? =
    trailers
        .asSequence()
        .filter { it.isPlayableYouTubeTrailerCandidate() }
        .distinctBy { it.key }
        .maxWithOrNull(
            compareBy<MetaTrailer>(
                { it.heroTrailerPriority() },
                { it.publishedAt.orEmpty() },
                { it.size ?: 0 },
                { it.name },
            ),
        )

internal fun MetaTrailer.youtubePlaybackUrl(): String =
    key.takeIf { it.startsWith("http://") || it.startsWith("https://") }
        ?: "https://www.youtube.com/watch?v=$key"

private fun MetaTrailer.isPlayableYouTubeTrailerCandidate(): Boolean =
    key.isNotBlank() && site.equals("YouTube", ignoreCase = true)

private fun MetaTrailer.heroTrailerPriority(): Int {
    val isSeriesTrailer = seasonNumber != null
    val isTrailerType = type.equals("Trailer", ignoreCase = true)
    return when {
        !isSeriesTrailer && isTrailerType && official -> 70
        !isSeriesTrailer && isTrailerType -> 60
        !isSeriesTrailer && official -> 50
        !isSeriesTrailer -> 40
        isTrailerType && official -> 30
        isTrailerType -> 20
        official -> 10
        else -> 0
    }
}

/** Try alternate TMDB/addon YouTube links when the preferred upload is unavailable. */
internal suspend fun resolveHeroTrailerPlaybackSource(
    trailers: List<MetaTrailer>,
    resolve: suspend (String) -> com.nuvio.app.features.trailer.TrailerPlaybackSource? =
        com.nuvio.app.features.trailer.TrailerPlaybackResolver::resolveFromYouTubeUrl,
): com.nuvio.app.features.trailer.TrailerPlaybackSource? {
    val remaining = trailers.toMutableList()
    var attempts = 0
    while (remaining.isNotEmpty() && attempts++ < 3) {
        val candidate = selectHeroTrailer(remaining) ?: return null
        remaining.removeAll { it.key == candidate.key }
        val source = try {
            kotlinx.coroutines.withTimeoutOrNull(15_000L) { resolve(candidate.youtubePlaybackUrl()) }
        } catch (cancellation: kotlinx.coroutines.CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            null
        }
        if (source != null) return source
    }
    return null
}
