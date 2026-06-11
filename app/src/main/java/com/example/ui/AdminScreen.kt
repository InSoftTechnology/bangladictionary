package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdManager
import com.example.data.DictionaryWord

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: DictionaryViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
    val pendingWords by viewModel.pendingWords.collectAsState()
    
    val totalCount by viewModel.totalWordsCount.collectAsState()
    val enToBnCount by viewModel.enToBnCount.collectAsState()
    val bnToEnCount by viewModel.bnToEnCount.collectAsState()
    
    val isAdsEnabled by AdManager.isAdsEnabled.collectAsState()
    val rewardPoints by AdManager.rewardedPoints.collectAsState()

    var adminCodeInput by remember { mutableStateOf("") }
    
    // Core Form state
    var wordVal by remember { mutableStateOf("") }
    var translationVal by remember { mutableStateOf("") }
    var pronunciationVal by remember { mutableStateOf("") }
    var partOfSpeechVal by remember { mutableStateOf("Noun") }
    var definitionVal by remember { mutableStateOf("") }
    var exampleVal by remember { mutableStateOf("") }
    var exampleTransVal by remember { mutableStateOf("") }
    var synonymsVal by remember { mutableStateOf("") }
    var antonymsVal by remember { mutableStateOf("") }
    var languageDirection by remember { mutableStateOf("en_to_bn") }

    // Bulk Importer variables
    var activeSubOption by remember { mutableStateOf(0) } // 0 = Add Word, 1 = Bulk Importer, 2 = Pending approvals
    var importTextData by remember { mutableStateOf("") }
    var isCsvFormat by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = "Admin Area Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Developer Admin Portal",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (!isAdminLoggedIn) {
            // LOGIN CARD (Passcode Prompt)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .testTag("admin_login_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Lock Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Access Protected Profile",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Enter developer authorization PIN to unlock administrator tools, analytics dashboards, and custom CSV/JSON seeders.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = adminCodeInput,
                        onValueChange = { adminCodeInput = it },
                        label = { Text("Developer Security Code") },
                        placeholder = { Text("PIN inside guide is: admin123") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_passcode_field"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val success = viewModel.loginAdmin(adminCodeInput)
                            if (success) {
                                adminCodeInput = ""
                                Toast.makeText(context, "Logged in as Administrator", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Incorrect PIN. Try 'admin123'", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_login_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Verify Developer Access", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Note: To support guest contributions, you can still submit new words below without logging in. They will save details locally and await approval!",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // ADMIN CONTROLS AND DASHBOARD DISPLAY
            // 1. Analytics Cards Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("TOTAL WORDS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        Text("$totalCount", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text("En: $enToBnCount | Bn: $bnToEnCount", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("REWARD BALANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                        Text("$rewardPoints pts", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Text("Earned from sponsor video", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            // 2. Control toggles (Ads enablement simulated remote configuration settings!)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "SIMULATED SYSTEM CONTROLS (REMOTE CONFIG)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 1.1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isAdsEnabled) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                                contentDescription = "Ads Switch",
                                tint = if (isAdsEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Monetization Ads Banner", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Toggle simulated bottom banner display", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Switch(
                            checked = isAdsEnabled,
                            onCheckedChange = {
                                AdManager.setAdsEnabled(it)
                                Toast.makeText(context, "Remote Config: Ads ${if (it) "ENABLED" else "DISABLED"}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("admin_ads_switch")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    // Buttons to launch simulated interstitials / rewarded video ads
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                AdManager.showInterstitial {
                                    Toast.makeText(context, "Interstitial ad completed successfully", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("trigger_test_interstitial"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.AdsClick, contentDescription = "Ads", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Interstitial", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                AdManager.showRewarded {
                                    Toast.makeText(context, "Points award given!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("trigger_test_rewarded"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PlayCircle, contentDescription = "Video", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Rewarded", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Button(
                        onClick = {
                            viewModel.logoutAdmin()
                            Toast.makeText(context, "Logged out of admin panel", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("admin_logout_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                    ) {
                        Text("Lock & Log Out", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Sub Navigation bar inside Admin Console
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(4.dp)
            ) {
                listOf("Add Single", "Bulk Import", "Approvals (${pendingWords.size})").forEachIndexed { index, optionName ->
                    val isSelected = activeSubOption == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { activeSubOption = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            optionName,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 3. Sub Operations Display (Add Word form holds even if Admin is logged out so users can submit)
        if (isAdminLoggedIn && activeSubOption == 1) {
            // ---- BULK IMPORTER ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    "BULK DICTIONARY SEEDER",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Expand database efficiently. Paste a valid JSON array of object maps or comma-separated CSV spreadsheets directly to parse and insert to Room.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isCsvFormat = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isCsvFormat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (!isCsvFormat) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("JSON Seeder")
                    }

                    Button(
                        onClick = { isCsvFormat = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCsvFormat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isCsvFormat) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("CSV Spreadsheet")
                    }
                }

                OutlinedTextField(
                    value = importTextData,
                    onValueChange = { importTextData = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = {
                        if (!isCsvFormat) {
                            Text(
                                "[\n  {\n    \"word\": \"smartphone\",\n    \"translation\": \"স্মার্টফোন\",\n    \"language\": \"en_to_bn\",\n    \"partOfSpeech\": \"Noun\"\n  }\n]"
                            )
                        } else {
                            Text(
                                "smartphone,স্মার্টফোন,en_to_bn,Noun,An offline mobile computing device.\nlaptop,ল্যাপটপ,en_to_bn,Noun,A portable personal computer."
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val demoText = if (isCsvFormat) {
                                """
                                smartphone,স্মার্টফোন,en_to_bn,Noun,An offline mobile phone
                                laptop,ল্যাপটপ,en_to_bn,Noun,A mobile personal computer
                                নদী,River,bn_to_en,Noun (বিশেষ্য),জলপ্রবাহ
                                """.trimIndent()
                            } else {
                                """
                                [
                                  {
                                    "word": "smartphone",
                                    "translation": "স্মার্টফোন",
                                    "language": "en_to_bn",
                                    "partOfSpeech": "Noun",
                                    "definition": "An offline intelligence mobile computing device."
                                  },
                                  {
                                    "word": "নদী",
                                    "translation": "River",
                                    "language": "bn_to_en",
                                    "partOfSpeech": "Noun (বিশেষ্য)",
                                    "definition": "প্রাকৃতিক জলধারা যা সাগর বা হ্রদে পতিত হয়।"
                                  }
                                ]
                                """.trimIndent()
                            }
                            importTextData = demoText
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Fill Sample Template")
                    }

                    Button(
                        onClick = {
                            if (importTextData.isBlank()) {
                                Toast.makeText(context, "Please paste or format input text first!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val success = if (isCsvFormat) {
                                viewModel.importFromCsv(importTextData)
                            } else {
                                viewModel.importFromJson(importTextData)
                            }

                            if (success) {
                                Toast.makeText(context, "Successfully Imported database values!", Toast.LENGTH_SHORT).show()
                                importTextData = ""
                            } else {
                                Toast.makeText(context, "Parsing failed. Please check syntax syntax structure.", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.weight(1f).testTag("admin_bulk_import_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Run Seeding Job", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else if (isAdminLoggedIn && activeSubOption == 2) {
            // ---- WORD APPROVAL SYSTEM ----
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "SUBMISSIONS REVIEW QUEUE (${pendingWords.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (pendingWords.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "All guest submissions have been audited! Outstanding queue backlog is complete.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                } else {
                    pendingWords.forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.word, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                                        Text(item.translation, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Text("Lang: ${item.language} | POS: ${item.partOfSpeech}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                    
                                    Badge(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer) {
                                        Text("Pending Approval", fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }

                                if (item.definition.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Meaning: ${item.definition}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.deleteWord(item)
                                            Toast.makeText(context, "Deleted submission", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Reject & Delete", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.approvePendingWord(item.id)
                                            Toast.makeText(context, "Approved Word successfully!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        modifier = Modifier.weight(1f).testTag("action_approve_word_${item.id}")
                                    ) {
                                        Text("Audit & Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ---- WORK SUBMIT FORM (SINGLE INSERTER) ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    text = if (isAdminLoggedIn) "ADD DICTIONARY WORD DIRECTLY" else "SUGGEST NEW DICTIONARY WORD",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isAdminLoggedIn)
                        "Enter the translation item details below. Submitting will insert the item directly into the approved dictionary library."
                    else
                        "Found a missing translation term? Submit it here to store it in the queue, waiting for admin approval!",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Lang Selector inside Form
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(2.dp)
                ) {
                    listOf("English ➔ Bangla", "Bangla ➔ English").forEachIndexed { index, langLabel ->
                        val isSel = (index == 0 && languageDirection == "en_to_bn") || (index == 1 && languageDirection == "bn_to_en")
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { languageDirection = if (index == 0) "en_to_bn" else "bn_to_en" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                langLabel,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Grid of Main Fields
                OutlinedTextField(
                    value = wordVal,
                    onValueChange = { wordVal = it },
                    label = { Text("Word *") },
                    placeholder = { Text(if (languageDirection == "en_to_bn") "e.g. computer" else "যেমন: কম্পিউটার") },
                    modifier = Modifier.fillMaxWidth().testTag("form_input_word"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = translationVal,
                    onValueChange = { translationVal = it },
                    label = { Text("Translation Meanings *") },
                    placeholder = { Text(if (languageDirection == "en_to_bn") "যেমন: গণকযন্ত্র / কম্পিউটার" else "e.g. computer") },
                    modifier = Modifier.fillMaxWidth().testTag("form_input_translation"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = pronunciationVal,
                        onValueChange = { pronunciationVal = it },
                        label = { Text("Pronunciation Guide") },
                        placeholder = { Text("e.g. kompiuṭar") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = partOfSpeechVal,
                        onValueChange = { partOfSpeechVal = it },
                        label = { Text("POS (Part of Speech)") },
                        placeholder = { Text("Noun, Verb, Adj") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = definitionVal,
                    onValueChange = { definitionVal = it },
                    label = { Text("Detailed Definition") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = exampleVal,
                    onValueChange = { exampleVal = it },
                    label = { Text("Example Sentence") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = exampleTransVal,
                    onValueChange = { exampleTransVal = it },
                    label = { Text("Example Translation") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = synonymsVal,
                    onValueChange = { synonymsVal = it },
                    label = { Text("Synonyms (comma-separated)") },
                    placeholder = { Text("e.g. PC, laptop, processor") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = antonymsVal,
                    onValueChange = { antonymsVal = it },
                    label = { Text("Antonyms (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (wordVal.isBlank() || translationVal.isBlank()) {
                            Toast.makeText(context, "Word and Translations are mandatory!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        viewModel.addNewWord(
                            word = wordVal,
                            translation = translationVal,
                            pronunciation = pronunciationVal,
                            partOfSpeech = partOfSpeechVal,
                            definition = definitionVal,
                            example = exampleVal,
                            exampleTrans = exampleTransVal,
                            synonymsList = synonymsVal,
                            antonymsList = antonymsVal,
                            langDirection = languageDirection
                        )

                        val msg = if (isAdminLoggedIn) "Word added successfully!" else "Suggested translation sent to review queue!"
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

                        // Reset Form
                        wordVal = ""
                        translationVal = ""
                        pronunciationVal = ""
                        partOfSpeechVal = "Noun"
                        definitionVal = ""
                        exampleVal = ""
                        exampleTransVal = ""
                        synonymsVal = ""
                        antonymsVal = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_submit_word_form_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        if (isAdminLoggedIn) "Save Dictionary Entry" else "Submit Suggestion for Audit",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}
