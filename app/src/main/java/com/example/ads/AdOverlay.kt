package com.example.ads

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val isEnabled by AdManager.isAdsEnabled.collectAsState()

    if (isEnabled) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("banner_ad_panel"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                "AD",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "English Grammar Pro Course",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        "AdMob Sponsor - Master spoken English with Forhad Labs!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                Button(
                    onClick = { /* Simulated link */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("ad_banner_action_button")
                ) {
                    Text("Install", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun FullScreenAdShowcase() {
    val activeAd by AdManager.currentActiveAd.collectAsState()

    if (activeAd != null) {
        var countdown by remember { mutableStateOf(5) }

        LaunchedEffect(activeAd) {
            countdown = if (activeAd is AdManager.AdProgress.Rewarded) 7 else 4
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
        }

        Dialog(
            onDismissRequest = {
                if (countdown <= 0) AdManager.dismissAd(rewardEarned = true)
            },
            properties = DialogProperties(
                dismissOnBackPress = countdown <= 0,
                dismissOnClickOutside = countdown <= 0,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (activeAd is AdManager.AdProgress.Rewarded) Icons.Default.PlayCircleFilled else Icons.Default.MonetizationOn,
                        contentDescription = "Ad Icon",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp)
                    )

                    Text(
                        text = if (activeAd is AdManager.AdProgress.Rewarded) "APPLOVIN REWARDED SPONSOR" else "GOOG ADMOB INTERSTITIAL",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (activeAd is AdManager.AdProgress.Rewarded)
                            "Unlock Bangla Dictionary Premium Vocabulary Pack for 24 Hours!"
                        else
                            "Boost Your Bengali Accent - Try TTS Dictionary offline speech companion!",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (activeAd is AdManager.AdProgress.Rewarded)
                            "Watch this short sponsored video to gain 100 Reward Points!"
                        else
                            "Interactive dictionary items are brought to you by Forhad Labs.",
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    if (countdown > 0) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Closing in $countdown seconds",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    } else {
                        Button(
                            onClick = { AdManager.dismissAd(rewardEarned = true) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .testTag("ad_dismiss_claim_button")
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                if (activeAd is AdManager.AdProgress.Rewarded) "Claim Reward & Close" else "Close Advertisement",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Close button top-right (active only if countdown completes)
                if (countdown <= 0) {
                    IconButton(
                        onClick = { AdManager.dismissAd(rewardEarned = true) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.DarkGray.copy(alpha = 0.5f), RoundedCornerShape(50.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Ad",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
