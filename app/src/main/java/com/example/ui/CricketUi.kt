package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sin

@Composable
fun CricketAppContent(viewModel: CricketViewModel) {
    val currentScreen = viewModel.currentScreen
    val isDark = viewModel.isDarkMode

    val darkScheme = darkColorScheme(
        primary = Color(0xFF3B82F6), // Sleek Royal Blue
        secondary = Color(0xFFF59E0B), // Sleek Amber
        tertiary = Color(0xFFEF4444), // Sleek Red Accent (OUT/Live)
        background = Color(0xFF0F172A), // Dark slate stadium navy background
        surface = Color(0xFF1E293B), // Dark slate surface
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color(0xFFF8FAFC),
        onSurface = Color(0xFFF1F5F9),
        surfaceVariant = Color(0xFF334155),
        onSurfaceVariant = Color(0xFF94A3B8),
        outline = Color(0xFF475569)
    )

    val lightScheme = lightColorScheme(
        primary = Color(0xFF2563EB), // Royal Blue for light mode
        secondary = Color(0xFFD97706), // Amber for light mode
        tertiary = Color(0xFFDC2626), // Crimson for light mode
        background = Color(0xFFF8FAFC), // Ice-slate light background
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFF0F172A),
        onSurface = Color(0xFF1E293B),
        surfaceVariant = Color(0xFFF1F5F9),
        onSurfaceVariant = Color(0xFF64748B),
        outline = Color(0xFFCBD5E1)
    )

    MaterialTheme(
        colorScheme = if (isDark) darkScheme else lightScheme,
        typography = MaterialTheme.typography
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.background
        ) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    ScreenState.WELCOME -> WelcomeScreen(viewModel)
                    ScreenState.AUTH -> AuthScreen(viewModel)
                    ScreenState.MATCH_LIST -> MatchListScreen(viewModel)
                    ScreenState.TEAM_SETUP -> TeamSetupScreen(viewModel)
                    ScreenState.MATCH_SETUP -> MatchSetupScreen(viewModel)
                    ScreenState.SCORING -> LiveScoringScreen(viewModel)
                    ScreenState.RESULT -> MatchResultScreen(viewModel)
                    ScreenState.LEADERBOARD -> LeaderboardDashboard(viewModel)
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(viewModel: CricketViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Cricket Ball Animation Canvas
            val infiniteTransition = rememberInfiniteTransition(label = "WelcomeBallAnim")
            val floatVal by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "FloatingBall"
            )
            val rotationVal by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(6000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "RotatingBall"
            )

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(y = (floatVal * -12).dp)
                    .drawBehind {
                        // Drawing professional leather cricket ball
                        drawCircle(
                            color = Color(0xFFEF4444),
                            radius = size.minDimension / 2
                        )
                        // Major horizontal stitch seam
                        val cY = size.height / 2
                        val cX = size.width / 2
                        drawLine(
                            color = Color.White,
                            start = Offset(0f, cY),
                            end = Offset(size.width, cY),
                            strokeWidth = 6f
                        )
                        // Diagonal stitches
                        for (i in 10..size.width.toInt() step 15) {
                            drawLine(
                                color = Color(0xFFE0E0E0),
                                start = Offset(i.toFloat(), cY - 8f),
                                end = Offset(i.toFloat() + 4f, cY + 8f),
                                strokeWidth = 3f
                            )
                        }
                    }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "क्रिकेट स्कोरर",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "एक संपूर्ण पेशेवर क्रिकेट स्कोरिंग ऐप",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Role Buttons
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "लॉगिन भूमिका चुनें (Role)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.selectRole(UserRole.ADMIN) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "स्कोरर लॉगिन (एडमिन)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { viewModel.selectRole(UserRole.VIEWER) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "दर्शक लाइव स्कोर्स (Viewer)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { 
                    viewModel.currentScreen = ScreenState.LEADERBOARD
                }
            ) {
                Icon(Icons.Filled.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "टूर्नामेंट आँकड़े और लीडरबोर्ड देखें",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                )
            }
        }
    }
}

@Composable
fun AuthScreen(viewModel: CricketViewModel) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.align(Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    viewModel.currentScreen = ScreenState.WELCOME
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "पीछे जाएँ")
                }
                Text("व्यवस्थापक पहुँच", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "एडमिन लॉगिन",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                "डेटा रिकॉर्डिंग और मैच सेटिंग्स शुरू करने के लिए लॉगिन करें",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = viewModel.adminMobile,
                        onValueChange = { if (it.length <= 10) viewModel.adminMobile = it },
                        label = { Text("मोबाइल नंबर") },
                        leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedVisibility(visible = viewModel.isOtpSent) {
                        Column {
                            OutlinedTextField(
                                value = viewModel.adminOtp,
                                onValueChange = { if (it.length <= 6) viewModel.adminOtp = it },
                                label = { Text("6-अंकीय ओटीपी (OTP)") },
                                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    if (viewModel.authError.isNotEmpty()) {
                        val isSuccessMsg = viewModel.authError.contains("भेजा गया") || viewModel.authError.contains("भर गया है") || viewModel.authError.contains("सफलता")
                        Text(
                            text = viewModel.authError,
                            color = if (isSuccessMsg) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            if (!viewModel.isOtpSent) {
                                viewModel.triggerSendOtp()
                            } else {
                                viewModel.verifyOtp()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (!viewModel.isOtpSent) "ओटीपी भेजें" else "लॉगिन सत्यापित करें",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MatchListScreen(viewModel: CricketViewModel) {
    val matches by viewModel.allMatchesFlow.collectAsState()
    val isDark = viewModel.isDarkMode

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App bar top
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (viewModel.userRole == UserRole.ADMIN) "एडमिन कंट्रोल" else "लाइव स्कोरबोर्ड",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.isDarkMode = !isDark }) {
                    Icon(
                        imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        contentDescription = "Theme Toggle",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                IconButton(onClick = { viewModel.currentScreen = ScreenState.WELCOME }) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = "भूमिका बदलें")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Large Tournament Leaderboard card trigger
        Card(
            onClick = { viewModel.currentScreen = ScreenState.LEADERBOARD },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🏆",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            "टूर्नामेंट लीडरबोर्ड",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "सबसे अधिक रन, सबसे अधिक विकेट",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (viewModel.userRole == UserRole.ADMIN) {
            Button(
                onClick = { viewModel.selectUpcomingMatchSetup() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("नया मैच शुरू करें", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "सभी मैच (Match Records)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (matches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏏", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "कोई सक्रिय मैच दर्ज नहीं है।",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    if (viewModel.userRole == UserRole.ADMIN) {
                        Text(
                            "मैच शुरू करने के लिए ऊपर दिए गए बटन पर टैप करें।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(matches) { match ->
                    MatchRowCard(match, viewModel)
                }
            }
        }
    }
}

@Composable
fun MatchRowCard(match: MatchEntity, viewModel: CricketViewModel) {
    val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateStr = formatter.format(Date(match.date))

    Card(
        onClick = {
            viewModel.loadMatch(match.id)
            if (match.status == "FINISHED") {
                viewModel.currentScreen = ScreenState.RESULT
            } else {
                viewModel.currentScreen = ScreenState.SCORING
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                // Status Badge
                val (statusText, badgeColor, textColor) = when (match.status) {
                    "SETUP" -> Triple("तैयारी", MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), MaterialTheme.colorScheme.secondary)
                    "LIVE" -> Triple("लाइव 🔴", MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f), MaterialTheme.colorScheme.tertiary)
                    "INNINGS_BREAK" -> Triple("हाफटाइम", MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), MaterialTheme.colorScheme.primary)
                    else -> Triple("पूर्ण", Color.Gray.copy(alpha = 0.2f), Color.Gray)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Team A info
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                    Text(
                        match.teamAName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (match.ballsIn1 > 0 || match.runsIn1 > 0) "${match.runsIn1}/${match.wicketsIn1} (${match.ballsIn1 / 6}.${match.ballsIn1 % 6} ओ)" else "पहले बल्लेबाजी",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    "बनाम",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Team B info
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        match.teamBName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (match.ballsIn2 > 0 || match.runsIn2 > 0) "${match.runsIn2}/${match.wicketsIn2} (${match.ballsIn2 / 6}.${match.ballsIn2 % 6} ओ)" else "पारी आनी बाकी",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.End
                    )
                }
            }

            if (match.status == "FINISHED" && match.winnerMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = match.winnerMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun TeamSetupScreen(viewModel: CricketViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var aNameInput by remember { mutableStateOf(viewModel.teamAName) }
    var bNameInput by remember { mutableStateOf(viewModel.teamBName) }
    var inputError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                viewModel.currentScreen = ScreenState.MATCH_LIST
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "पीछे")
            }
            Text("टीमें सेटअप करें", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Teams Names Card
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "टीमों के नाम",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = aNameInput,
                    onValueChange = { aNameInput = it; viewModel.teamAName = it },
                    label = { Text("टीम ए (Team A) का नाम") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = bNameInput,
                    onValueChange = { bNameInput = it; viewModel.teamBName = it },
                    label = { Text("टीम बी (Team B) का नाम") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Team A Players
        PlayersSetupCard(
            teamName = aNameInput,
            playersList = viewModel.teamAPlayers,
            cardColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Team B Players
        PlayersSetupCard(
            teamName = bNameInput,
            playersList = viewModel.teamBPlayers,
            cardColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (inputError.isNotEmpty()) {
            Text(
                inputError,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(bottom = 12.dp),
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                if (viewModel.teamAPlayers.size < 1 || viewModel.teamBPlayers.size < 1) {
                    inputError = "प्रत्येक टीम में कम से कम 1 खिलाड़ी होना आवश्यक है!"
                } else if (aNameInput.trim().isEmpty() || bNameInput.trim().isEmpty()) {
                    inputError = "दोनों टीमों का नाम भरना आवश्यक है!"
                } else {
                    inputError = ""
                    viewModel.saveTeamsAndMoveToMatchSetup()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("टॉस और मैच सेटअप की ओर बढ़ें", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun PlayersSetupCard(teamName: String, playersList: MutableList<String>, cardColor: Color) {
    var newPlayerName by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$teamName के खिलाड़ी (${playersList.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("कम से कम 1", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Player names scrollable listing or editable column
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                playersList.forEachIndexed { index, player ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Text("${index + 1}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))

                        var isEditing by remember { mutableStateOf(false) }
                        var editedText by remember { mutableStateOf(player) }

                        if (isEditing) {
                            OutlinedTextField(
                                value = editedText,
                                onValueChange = { editedText = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(onClick = {
                                        if (editedText.trim().isNotEmpty()) {
                                            playersList[index] = editedText
                                            isEditing = false
                                        }
                                    }) {
                                        Icon(Icons.Filled.Check, contentDescription = "सहेजें")
                                    }
                                }
                            )
                        } else {
                            Text(
                                text = player,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { isEditing = true },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        IconButton(onClick = {
                            if (playersList.size > 1) {
                                playersList.removeAt(index)
                            }
                        }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "हटाएं",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newPlayerName,
                    onValueChange = { newPlayerName = it },
                    placeholder = { Text("खिलाड़ी का नाम दर्ज करें") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newPlayerName.trim().isNotEmpty()) {
                                playersList.add(newPlayerName.trim())
                                newPlayerName = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("जोड़ें")
                    }
                }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchSetupScreen(viewModel: CricketViewModel) {
    var selectedBatsman1 by remember { mutableStateOf("") }
    var selectedBatsman2 by remember { mutableStateOf("") }
    var selectedBowler by remember { mutableStateOf("") }

    val battingPlayers = if (viewModel.battingFirstTeam == viewModel.teamAName) viewModel.teamAPlayers else viewModel.teamBPlayers
    val bowlingPlayers = if (viewModel.battingFirstTeam == viewModel.teamAName) viewModel.teamBPlayers else viewModel.teamAPlayers

    var currentOversText by remember { mutableStateOf(viewModel.totalOvers) }

    // Init fields
    LaunchedEffect(Unit) {
        if (battingPlayers.size >= 2) {
            selectedBatsman1 = battingPlayers[0]
            selectedBatsman2 = battingPlayers[1]
        }
        if (bowlingPlayers.isNotEmpty()) {
            selectedBowler = bowlingPlayers[0]
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.currentScreen = ScreenState.TEAM_SETUP }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "पीछे")
            }
            Text("मैच सेटअप (ओवर्स और ओपनर्स)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Match Conditions Card
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "मैच सेटिंग्स",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = currentOversText,
                    onValueChange = {
                        currentOversText = it
                        viewModel.totalOvers = it
                    },
                    label = { Text("कुल ओवरों की संख्या (Overs)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "पहले कौन बल्लेबाजी करेगा?",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.battingFirstTeam = viewModel.teamAName }
                    ) {
                        RadioButton(
                            selected = viewModel.battingFirstTeam == viewModel.teamAName,
                            onClick = { viewModel.battingFirstTeam = viewModel.teamAName }
                        )
                        Text(viewModel.teamAName, style = MaterialTheme.typography.bodyMedium)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.battingFirstTeam = viewModel.teamBName }
                    ) {
                        RadioButton(
                            selected = viewModel.battingFirstTeam == viewModel.teamBName,
                            onClick = { viewModel.battingFirstTeam = viewModel.teamBName }
                        )
                        Text(viewModel.teamBName, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Select Openers Card
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ओपनिंग खिलाड़ी चुनें",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Batsman 1 (Striker) Dropdown
                Text("सलामी बल्लेबाज 1 (स्ट्राइकर)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                SpinnerSelector(
                    options = battingPlayers,
                    selectedOption = selectedBatsman1,
                    onOptionSelected = { selectedBatsman1 = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Batsman 2 (Non Striker) Dropdown
                Text("सलामी बल्लेबाज 2 (नॉन-स्ट्राइकर)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                SpinnerSelector(
                    options = battingPlayers.filter { it != selectedBatsman1 },
                    selectedOption = selectedBatsman2,
                    onOptionSelected = { selectedBatsman2 = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Bowler Dropdown
                Text("शुरुआती गेंदबाज (Bowler)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                SpinnerSelector(
                    options = bowlingPlayers,
                    selectedOption = selectedBowler,
                    onOptionSelected = { selectedBowler = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (selectedBatsman1.isEmpty() || selectedBatsman2.isEmpty() || selectedBowler.isEmpty()) {
                    // Quick validation toast
                } else if (currentOversText.toIntOrNull() == null || currentOversText.toInt() <= 0) {
                    // validation overs
                } else {
                    viewModel.createAndStartMatch(selectedBatsman1, selectedBatsman2, selectedBowler)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.SportsCricket, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("मैच और स्कोरिंग शुरू करें!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SpinnerSelector(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { expanded = true }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedOption.ifEmpty { "खिलाड़ी का चयन करें" },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun LiveScoringScreen(viewModel: CricketViewModel) {
    val match by viewModel.activeMatchFlow.collectAsState()
    val matchId = viewModel.activeMatchId
    val players by viewModel.activeMatchPlayersFlow.collectAsState()
    val commentaryList by viewModel.commentaryFlow.collectAsState()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    if (match == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val activeMatch = match!!

    // Auto compute batting team
    val battingTeam = if (activeMatch.currentInnings == 1) activeMatch.battingFirstTeam else {
        if (activeMatch.battingFirstTeam == activeMatch.teamAName) activeMatch.teamBName else activeMatch.teamAName
    }
    val bowlingTeam = if (battingTeam == activeMatch.teamAName) activeMatch.teamBName else activeMatch.teamAName

    val currentRuns = if (activeMatch.currentInnings == 1) activeMatch.runsIn1 else activeMatch.runsIn2
    val currentWickets = if (activeMatch.currentInnings == 1) activeMatch.wicketsIn1 else activeMatch.wicketsIn2
    val currentBalls = if (activeMatch.currentInnings == 1) activeMatch.ballsIn1 else activeMatch.ballsIn2
    val currentExtras = if (activeMatch.currentInnings == 1) activeMatch.extrasIn1 else activeMatch.extrasIn2

    val displayOvers = "${currentBalls / 6}.${currentBalls % 6}"
    val crr = if (currentBalls > 0) String.format("%.2f", (currentRuns.toDouble() / currentBalls) * 6.0) else "0.00"

    var selectedTab by remember { mutableStateOf(0) } // 0: Scoring, 1: Scorecard, 2: Commentary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // App top row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.currentScreen = ScreenState.MATCH_LIST }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "वापस जाएँ")
                }
                Text("मैच विवरण # ${activeMatch.id}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Row {
                IconButton(onClick = {
                    val shareUrl = "https://ais-pre-okp7l4l6engxfgq3rwskx3-42753156011.asia-east1.run.app"
                    clipboard.setText(AnnotatedString(shareUrl))
                    Toast.makeText(context, "लाइव स्कोर शेयरिंग लिंक कॉपी हो गया है!", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Filled.Share, contentDescription = "स्कोर शेयर करें", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // LARGE DIGITAL SCOREBOARD CARD
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        battingTeam,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (activeMatch.currentInnings == 1) "पहली पारी" else "दूसरी पारी",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Gray.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // LARGE SCORE VALUE
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$currentRuns",
                        fontSize = 56.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "/ $currentWickets",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                    )
                }

                Text(
                    text = "ओवर: $displayOvers   (कुल ${activeMatch.totalOvers} ओवर)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("रन रेट (CRR)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text(crr, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    if (activeMatch.currentInnings == 2) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("लक्ष्य (Target)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text("${activeMatch.target}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        }

                        val ballsRemaining = (activeMatch.totalOvers * 6) - currentBalls
                        val runsNeeded = activeMatch.target - currentRuns
                        val rrr = if (ballsRemaining > 0) String.format("%.2f", (runsNeeded.toDouble() / ballsRemaining) * 6.0) else "0.00"

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ज़रूरी (RRR)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(rrr, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("अतिरिक्त (Extras)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text("$currentExtras", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (activeMatch.currentInnings == 2) {
                    val runsNeeded = activeMatch.target - currentRuns
                    val ballsRemaining = (activeMatch.totalOvers * 6) - currentBalls
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "जीत के लिए $ballsRemaining गेंदों में $runsNeeded रनों की आवश्यकता है",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }

                if (activeMatch.status == "INNINGS_BREAK") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.startSecondInnings() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("दूसरी पारी शुरू करें (लक्ष्य: ${activeMatch.target})", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // TAB NAVIGATION
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.SportsCricket, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("स्कोरिंग पैड", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.People, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("स्कोरकार्ड", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Timeline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("कमेंट्री", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // TABS BODY
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> {
                    if (viewModel.userRole == UserRole.VIEWER) {
                        // Viewers locked out message or showing quick batters summary + balls list
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("🔒 दर्शक मोड (Viewer Mode)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("आप केवल लाइव स्कोर देख सकते हैं। स्कोरिंग नियंत्रण एडमिन के पास है।", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(8.dp))
                            Spacer(modifier = Modifier.height(20.dp))
                            LiveBatsmenBowlerSection(players, battingTeam, bowlingTeam, viewModel, true)
                        }
                    } else {
                        AdminScoringTab(viewModel, players, battingTeam, bowlingTeam, activeMatch)
                    }
                }
                1 -> ScorecardTab(players, battingTeam, bowlingTeam, commentaryList, activeMatch)
                2 -> CommentaryTab(commentaryList)
            }
        }
    }

    // Dynamic replacement dialogs
    if (viewModel.showBatsmanChangeDialog) {
        WicketNextBatsmanDialog(viewModel, players, battingTeam)
    }
    if (viewModel.showBowlerChangeDialog) {
        BowlerChangeDialog(viewModel, players, bowlingTeam, title = "ओवर समाप्त! नया गेंदबाज चुनें")
    }
    if (viewModel.showManualEditDialog) {
        ManualScoreCorrectionDialog(viewModel, currentRuns, currentWickets)
    }
}

@Composable
fun LiveBatsmenBowlerSection(
    players: List<MatchPlayerEntity>,
    battingTeam: String,
    bowlingTeam: String,
    viewModel: CricketViewModel,
    isViewerMode: Boolean
) {
    val currentBatsmen = players.filter { it.teamName == battingTeam && it.isCurrentBatsman }
    val currentBowler = players.find { it.teamName == bowlingTeam && it.isCurrentBowler }
    val eligibleBowlers = players.filter { it.teamName == bowlingTeam && !it.isCurrentBowler }
    val eligibleBatsmen = players.filter { it.teamName == battingTeam && !it.isCurrentBatsman && !it.isOut }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "चालू खिलाड़ी (Active Players)",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Batsmen Grid Row (2 Column split-screen layout)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Batter 1 (Usually Striker)
                    val b1 = currentBatsmen.getOrNull(0)
                    Box(modifier = Modifier.weight(1f)) {
                        if (b1 != null) {
                            var showChangeDrop by remember { mutableStateOf(false) }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (b1.isStriker) "बल्लेबाज *" else "बल्लेबाज",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (b1.isStriker) {
                                        Text(
                                            text = "स्ट्राइक",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = !isViewerMode) { showChangeDrop = true },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = b1.playerName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        val sr = if (b1.ballsFaced > 0) String.format("%.1f", (b1.runsScored.toDouble() / b1.ballsFaced) * 100) else "0.0"
                                        Text(
                                            text = "SR: $sr  4s: ${b1.fours} 6s: ${b1.sixes}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                        )
                                    }
                                    Text(
                                        text = "${b1.runsScored} (${b1.ballsFaced})",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }

                                DropdownMenu(expanded = showChangeDrop, onDismissRequest = { showChangeDrop = false }) {
                                    eligibleBatsmen.forEach { candidate ->
                                        DropdownMenuItem(
                                            text = { Text(candidate.playerName) },
                                            onClick = {
                                                viewModel.changeBatsmanOption(if (b1.isStriker) 1 else 2, candidate.playerName)
                                                showChangeDrop = false
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            Text("-", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        }
                    }

                    // Vertical partition line
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(55.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )

                    // Batter 2 (Non-Striker)
                    val b2 = currentBatsmen.getOrNull(1)
                    Box(modifier = Modifier.weight(1f)) {
                        if (b2 != null) {
                            var showChangeDrop by remember { mutableStateOf(false) }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (b2.isStriker) "बल्लेबाज *" else "बल्लेबाज",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (b2.isStriker) {
                                        Text(
                                            text = "स्ट्राइक",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = !isViewerMode) { showChangeDrop = true },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = b2.playerName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        val sr = if (b2.ballsFaced > 0) String.format("%.1f", (b2.runsScored.toDouble() / b2.ballsFaced) * 100) else "0.0"
                                        Text(
                                            text = "SR: $sr  4s: ${b2.fours} 6s: ${b2.sixes}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                        )
                                    }
                                    Text(
                                        text = "${b2.runsScored} (${b2.ballsFaced})",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }

                                DropdownMenu(expanded = showChangeDrop, onDismissRequest = { showChangeDrop = false }) {
                                    eligibleBatsmen.forEach { candidate ->
                                        DropdownMenuItem(
                                            text = { Text(candidate.playerName) },
                                            onClick = {
                                                viewModel.changeBatsmanOption(if (b2.isStriker) 1 else 2, candidate.playerName)
                                                showChangeDrop = false
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            Text("-", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        }
                    }
                }

                // Horizontal partition divider line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Bowler Section
                currentBowler?.let { bowler ->
                    var showChangeDrop by remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1.1f)
                                .clickable(enabled = !isViewerMode) { showChangeDrop = true }
                        ) {
                            Text(
                                text = "गेंदबाज",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = bowler.playerName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Stats columns: Over, Wicket, Economy
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1.3f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val overs = "${bowler.ballsBowled / 6}.${bowler.ballsBowled % 6}"
                            val eco = if (bowler.ballsBowled > 0) String.format("%.2f", bowler.runsConceded.toDouble() / bowler.ballsBowled * 6.0) else "0.00"

                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("ओवर", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(overs, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text("विकेट", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${bowler.wicketsTaken}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1.2f)) {
                                Text("इकोनॉमी", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(eco, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), color = Color(0xFF4ADE80))
                            }
                        }

                        DropdownMenu(expanded = showChangeDrop, onDismissRequest = { showChangeDrop = false }) {
                            eligibleBowlers.forEach { candidate ->
                                DropdownMenuItem(
                                    text = { Text(candidate.playerName) },
                                    onClick = {
                                        viewModel.selectNewBowler(candidate.playerName)
                                        showChangeDrop = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminScoringTab(
    viewModel: CricketViewModel,
    players: List<MatchPlayerEntity>,
    battingTeam: String,
    bowlingTeam: String,
    activeMatch: MatchEntity
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LiveBatsmenBowlerSection(players, battingTeam, bowlingTeam, viewModel, false)

        Divider()

        Text("रन रिकॉर्ड करें (Scoring Controls)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        // Grid of scoring runs
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(0, 1, 2, 3).forEach { run ->
                Button(
                    onClick = { viewModel.recordBall(run) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text("$run", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Hot buttons 4 and 6
            Button(
                onClick = { viewModel.recordBall(4) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("4 चौका", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            }

            Button(
                onClick = { viewModel.recordBall(6) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("6 छक्का", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }

        // Extras Controls & Wicket
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.recordBall(0, isWide = true) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Text("वाइड (WD)", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.recordBall(0, isNoBall = true) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Text("नो बॉल (NB)", fontWeight = FontWeight.Bold)
            }

            var showWicketDialog by remember { mutableStateOf(false) }
            Button(
                onClick = { showWicketDialog = true },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1.2f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("आउट (WKT) 🔴", fontWeight = FontWeight.Bold, color = Color.White)
            }

            if (showWicketDialog) {
                WicketTypeDialog(
                    onDismiss = { showWicketDialog = false },
                    onWicketSelected = { wktType ->
                        viewModel.recordBall(0, isWicket = true, wicketType = wktType)
                        showWicketDialog = false
                    }
                )
            }
        }

        Divider()

        // Corrective Management Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { viewModel.undoLastBall() },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.tertiary)
            ) {
                Icon(Icons.Filled.Undo, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("पूर्ववत (Undo)", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { viewModel.showManualEditDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("मैनुअल बदलाव", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ScorecardTab(
    players: List<MatchPlayerEntity>,
    battingTeam: String,
    bowlingTeam: String,
    commentaryList: List<BallEntity>,
    match: MatchEntity
) {
    val battingCard = players.filter { it.teamName == battingTeam && (it.battingOrder > 0 || it.isCurrentBatsman) }.sortedBy { it.battingOrder }
    val bowlingCard = players.filter { it.teamName == bowlingTeam && it.ballsBowled > 0 }

    val totalExtras = if (match.currentInnings == 1) match.extrasIn1 else match.extrasIn2
    val wicketsFallenList = commentaryList.filter { it.inningsNum == match.currentInnings && it.isWicket }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Batting Card Section
        item {
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("बल्लेबाजी स्कोरकार्ड ($battingTeam)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("बल्लेबाज", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("R", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
                        Text("B", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
                        Text("4s", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
                        Text("6s", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
                        Text("SR", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
                    }

                    Divider(modifier = Modifier.padding(vertical = 6.dp))

                    if (battingCard.isEmpty()) {
                        Text("अभी तक बल्लेबाजी शुरू नहीं हुई है।", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    } else {
                        battingCard.forEach { p ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.weight(2f)) {
                                    Text(
                                        "${p.playerName}${if (p.isCurrentBatsman) "*" else ""}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (p.isCurrentBatsman) "खेल रहा है" else if (p.isOut) p.wicketDescription else "नहीं खेला",
                                        fontSize = 11.sp,
                                        color = if (p.isCurrentBatsman) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                }
                                Text("${p.runsScored}", modifier = Modifier.weight(0.5f), fontSize = 13.sp, textAlign = TextAlign.End, fontWeight = FontWeight.Bold)
                                Text("${p.ballsFaced}", modifier = Modifier.weight(0.5f), fontSize = 13.sp, textAlign = TextAlign.End)
                                Text("${p.fours}", modifier = Modifier.weight(0.5f), fontSize = 13.sp, textAlign = TextAlign.End)
                                Text("${p.sixes}", modifier = Modifier.weight(0.5f), fontSize = 13.sp, textAlign = TextAlign.End)
                                val sr = if (p.ballsFaced > 0) String.format("%.1f", (p.runsScored.toDouble() / p.ballsFaced * 100)) else "0.0"
                                Text(sr, modifier = Modifier.weight(0.8f), fontSize = 13.sp, textAlign = TextAlign.End, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }
        }

        // Extras Section
        item {
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("अतिरिक्त (Extras):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text("$totalExtras", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Bowling Card Section
        item {
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("गेंदबाजी विश्लेषण ($bowlingTeam)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("गेंदबाज", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("O", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
                        Text("R", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
                        Text("W", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
                        Text("Econ", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
                    }

                    Divider(modifier = Modifier.padding(vertical = 6.dp))

                    if (bowlingCard.isEmpty()) {
                        Text("अभी तक गेंदबाजी शुरू नहीं हुई है।", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    } else {
                        bowlingCard.forEach { p ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(p.playerName, modifier = Modifier.weight(2f), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                val ovs = "${p.ballsBowled / 6}.${p.ballsBowled % 6}"
                                Text(ovs, modifier = Modifier.weight(0.7f), fontSize = 13.sp, textAlign = TextAlign.End)
                                Text("${p.runsConceded}", modifier = Modifier.weight(0.7f), fontSize = 13.sp, textAlign = TextAlign.End)
                                Text("${p.wicketsTaken}", modifier = Modifier.weight(0.7f), fontSize = 13.sp, textAlign = TextAlign.End, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                val econ = if (p.ballsBowled > 0) String.format("%.2f", (p.runsConceded.toDouble() / p.ballsBowled * 6)) else "0.00"
                                Text(econ, modifier = Modifier.weight(0.8f), fontSize = 13.sp, textAlign = TextAlign.End)
                            }
                        }
                    }
                }
            }
        }

        // Partnerships Section
        item {
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("साझेदारी (Partnership)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Divider(modifier = Modifier.padding(vertical = 6.dp))
                    
                    val currentBatsmen = players.filter { it.teamName == battingTeam && it.isCurrentBatsman }
                    if (currentBatsmen.size >= 2) {
                        val sumRuns = currentBatsmen[0].runsScored + currentBatsmen[1].runsScored
                        val sumBalls = currentBatsmen[0].ballsFaced + currentBatsmen[1].ballsFaced
                        Text(
                            text = "${currentBatsmen[0].playerName} और ${currentBatsmen[1].playerName}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "चालू साझेदारी: $sumRuns रन ($sumBalls गेंदें)",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text("सक्रिय साझेदारी उपलब्ध नहीं है।", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        // Fall of Wickets section
        item {
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("विकेटों का पतन (Fall of Wickets)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Divider(modifier = Modifier.padding(vertical = 6.dp))

                    if (wicketsFallenList.isEmpty()) {
                        Text("कोई विकेट नहीं गिरा है अभी तक।", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    } else {
                        wicketsFallenList.reversed().forEachIndexed { index, b ->
                            Text(
                                text = "${wicketsFallenList.size - index}. ${b.runs}/${wicketsFallenList.size - index} (${b.commentary.substringBefore("आउट")}) ओ: ${b.overNum}.${b.ballNum}",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 3.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentaryTab(commentaryList: List<BallEntity>) {
    if (commentaryList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("लाइव कमेंट्री शुरू होने की प्रतीक्षा की जा रही है...", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(commentaryList) { ball ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left vertical state indicator bar (representing HTML's border-l-2 pl-3 pattern)
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .fillMaxHeight()
                            .background(
                                if (ball.isWicket) MaterialTheme.colorScheme.tertiary 
                                else if (ball.runs == 4 || ball.runs == 6) MaterialTheme.colorScheme.primary 
                                else Color.Transparent
                            )
                    )

                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Over dot display
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (ball.isWicket) MaterialTheme.colorScheme.tertiary 
                                    else if (ball.runs == 4 || ball.runs == 6) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) 
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${ball.overNum}.${ball.ballNum}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (ball.isWicket) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            val runsTextColor = if (ball.isWicket) MaterialTheme.colorScheme.tertiary 
                                               else if (ball.runs == 4 || ball.runs == 6) MaterialTheme.colorScheme.primary 
                                               else MaterialTheme.colorScheme.onSurface

                            Text(
                                text = ball.commentary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (ball.isWicket || ball.runs >= 4) FontWeight.Bold else FontWeight.Normal,
                                color = runsTextColor
                            )
                            Text(
                                text = "${ball.batsmanName} बनाम ${ball.bowlerName}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WicketTypeDialog(onDismiss: () -> Unit, onWicketSelected: (String) -> Unit) {
    val types = listOf("बोल्ड (Bowled)", "कैच आउट (Caught)", "एलबीडब्ल्यू (LBW)", "रन आउट (Run Out)", "स्टम्पड (Stumped)", "हिट विकेट (Hit Wicket)")
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("आउट का प्रकार चुनें", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                types.forEach { type ->
                    Button(
                        onClick = { onWicketSelected(type) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Text(type, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("रद्द करें") }
        }
    )
}

@Composable
fun WicketNextBatsmanDialog(viewModel: CricketViewModel, players: List<MatchPlayerEntity>, battingTeam: String) {
    val eligibleBatsmen = players.filter { it.teamName == battingTeam && !it.isCurrentBatsman && !it.isOut }
    AlertDialog(
        onDismissRequest = { }, // Force select a batsman
        title = { Text("नया बल्लेबाज क्रीज पर", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("कृपया आउट हुए बल्लेबाज की जगह लेने के लिए नया खिलाड़ी चुनें:", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 240.dp)) {
                    items(eligibleBatsmen) { candidate ->
                        Button(
                            onClick = { viewModel.replaceOutBatsman(candidate.playerName) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(candidate.playerName, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun BowlerChangeDialog(viewModel: CricketViewModel, players: List<MatchPlayerEntity>, bowlingTeam: String, title: String) {
    val eligibleBowlers = players.filter { it.teamName == bowlingTeam }
    AlertDialog(
        onDismissRequest = { }, // Force select a bowler
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("अगले ओवर के लिए गेंदबाज का चयन करें:", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.heightIn(max = 240.dp)) {
                    items(eligibleBowlers) { candidate ->
                        Button(
                            onClick = { viewModel.selectNewBowler(candidate.playerName) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = Color.Black)
                        ) {
                            Text(candidate.playerName, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun ManualScoreCorrectionDialog(viewModel: CricketViewModel, currentRuns: Int, currentWickets: Int) {
    var runsVal by remember { mutableStateOf(currentRuns.toString()) }
    var wicketsVal by remember { mutableStateOf(currentWickets.toString()) }

    AlertDialog(
        onDismissRequest = { viewModel.showManualEditDialog = false },
        title = { Text("मैनुअल स्कोर बदलाव (Correction)", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("यदि स्कोरिंग दर्ज करने में कोई बड़ी विसंगति हुई है, तो यहाँ सीधे स्कोर संशोधित करें:")
                OutlinedTextField(
                    value = runsVal,
                    onValueChange = { runsVal = it; viewModel.overrideRuns = it },
                    label = { Text("कुल रन (Total Runs)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = wicketsVal,
                    onValueChange = { wicketsVal = it; viewModel.overrideWickets = it },
                    label = { Text("कुल विकेट (Total Wickets)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.applyManualOverride() }) {
                Text("स्कोर संशोधित करें")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.showManualEditDialog = false }) {
                Text("रद्द करें")
            }
        }
    )
}

@Composable
fun MatchResultScreen(viewModel: CricketViewModel) {
    val match by viewModel.activeMatchFlow.collectAsState()
    if (match == null) return
    val activeMatch = match!!

    // Canvas Confetti Particles Animation
    val infiniteTransition = rememberInfiniteTransition(label = "ConfettiAnim")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ConfettiTimeline"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(24.dp)
            .drawBehind {
                // Draw floating colorful stars confetti
                drawConfetti(animProgress)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Medal Shield Logo
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA000))))
        ) {
            Icon(
                Icons.Filled.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "विजेता घोषणा!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = activeMatch.winnerMessage,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // PLAYER OF THE MATCH (मैच का सर्वश्रेष्ठ खिलाड़ी) CARD - WITH PHOTO PLACEHOLDER
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = BorderStroke(2.dp, Color(0xFFFFD700)), // Gold Border
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Gold Medal Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏅", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "मैच का सर्वश्रेष्ठ खिलाड़ी (POM)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBC02D)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Placeholder Photo and Name
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = activeMatch.playerOfMatchName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = activeMatch.playerOfMatchReason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.currentScreen = ScreenState.MATCH_LIST },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("होम स्क्रीन पर वापस जाएँ", fontWeight = FontWeight.Bold)
        }
    }
}

private fun DrawScope.drawConfetti(progress: Float) {
    val r = Random(1234)
    for (i in 0..40) {
        val startX = r.nextFloat() * size.width
        val speedY = 200f + r.nextFloat() * 400f
        val curY = ((progress * speedY) % size.height)
        val particleRadius = 6f + r.nextFloat() * 10f
        
        val colorsList = listOf(
            Color(0xFFFFD700), // Gold
            Color(0xFF00C853), // Green
            Color(0xFFD32F2F), // Red
            Color(0xFF29B6F6), // Blue
            Color(0xFFAB47BC)  // Purple
        )
        val color = colorsList[r.nextInt(colorsList.size)]
        
        drawCircle(
            color = color.copy(alpha = 1.0f - (curY / size.height)),
            radius = particleRadius,
            center = Offset(startX + sin(progress * 8f + i) * 30f, curY)
        )
    }
}

@Composable
fun LeaderboardDashboard(viewModel: CricketViewModel) {
    val runsLeaders by viewModel.leaderboardRuns.collectAsState()
    val wicketsLeaders by viewModel.leaderboardWickets.collectAsState()

    var activeSubTab by remember { mutableStateOf(0) } // 0: Most Runs, 1: Most Wkts, 2: Rates

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.currentScreen = ScreenState.MATCH_LIST }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "पीछे")
            }
            Text("टूर्नामेंट लीडरबोर्ड (Leaderboards)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Leaderboard tabs
        TabRow(selectedTabIndex = activeSubTab) {
            Tab(selected = activeSubTab == 0, onClick = { activeSubTab = 0 }) {
                Text("सबसे अधिक रन 🏏", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeSubTab == 1, onClick = { activeSubTab = 1 }) {
                Text("सबसे अधिक विकेट 🔴", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val currentList = if (activeSubTab == 0) runsLeaders else wicketsLeaders

        if (currentList.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏅", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("लीडरबोर्ड लोड हो रहा है या आंकड़े अनुपलब्ध हैं।", color = Color.Gray)
                    Text("शुरू किए गए मैचों को समाप्त करने के बाद आंकड़े अपडेट होंगे।", fontSize = 11.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(currentList.take(20)) { player ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                ) {
                                    Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(player.name, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                                    Text(player.team, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                if (activeSubTab == 0) {
                                    Text("${player.runs} रन", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                    Text("${player.balls} ग | SR: ${String.format("%.1f", player.strikeRate)}", style = MaterialTheme.typography.bodySmall)
                                } else {
                                    Text("${player.wickets} विकेट", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.tertiary)
                                    val ovs = "${player.ballsBowled / 6}.${player.ballsBowled % 6}"
                                    Text("ओवर्स: $ovs | Econ: ${String.format("%.2f", player.economy)}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
