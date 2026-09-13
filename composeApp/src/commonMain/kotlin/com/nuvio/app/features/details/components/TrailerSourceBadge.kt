package com.nuvio.app.features.details.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.trailer_source_youtube
import org.jetbrains.compose.resources.stringResource

/** All current hero sources are YouTube videos, including links discovered via TMDB. */
@Composable
internal fun TrailerSourceBadge(modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(8.dp), color = Color.Black.copy(alpha = 0.65f)) {
        Text(
            text = stringResource(Res.string.trailer_source_youtube),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
        )
    }
}
