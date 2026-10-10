package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SyncState

@Composable
fun SyncStatusBadge(
    syncState: SyncState,
    onSyncClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, text, icon) = when (syncState) {
        SyncState.SYNCED -> Quad(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            "Synced",
            Icons.Default.CloudDone
        )
        SyncState.SYNC_PENDING -> Quad(
            Color(0xFFFFF3E0),
            Color(0xFFE65100),
            "Pending Sync",
            Icons.Default.CloudSync
        )
        SyncState.SYNCING -> Quad(
            Color(0xFFE3F2FD),
            Color(0xFF1565C0),
            "Syncing...",
            Icons.Default.CloudSync
        )
        SyncState.OFFLINE -> Quad(
            Color(0xFFECEFF1),
            Color(0xFF546E7A),
            "Offline Mode",
            Icons.Default.CloudOff
        )
        SyncState.SYNC_FAILED -> Quad(
            Color(0xFFFFEBEE),
            Color(0xFFC62828),
            "Sync Failed",
            Icons.Default.SyncProblem
        )
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSyncClick() },
        color = bgColor,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (syncState == SyncState.SYNCING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = textColor
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
