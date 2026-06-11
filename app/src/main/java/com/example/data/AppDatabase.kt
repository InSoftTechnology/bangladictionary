package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [DictionaryWord::class, SearchHistory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bangla_dictionary_database"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.wordDao())
                }
            }
        }

        private suspend fun populateInitialData(wordDao: WordDao) {
            val initialWords = listOf(
                // ---- ENGLISH TO BANGLA ----
                DictionaryWord(
                    word = "hello",
                    translation = "হ্যালো / ওহে",
                    pronunciation = "/həˈloʊ/ (হেলো / ওহে)",
                    partOfSpeech = "Interjection",
                    definition = "used as a greeting or to begin a phone conversation.",
                    exampleSentence = "She said hello to him with a smile.",
                    exampleTranslation = "সে তাকে মৃদু হেসে হ্যালো বলল।",
                    synonyms = "greetings, hi, hey, salutation",
                    antonyms = "goodbye, farewell",
                    language = "en_to_bn"
                ),
                DictionaryWord(
                    word = "dictionary",
                    translation = "অভিধান / শব্দকোষ",
                    pronunciation = "/ˈdɪkʃəneri/ (ডিকশনারি)",
                    partOfSpeech = "Noun",
                    definition = "a book or electronic resource that lists the words of a language in alphabetical order and gives their meaning.",
                    exampleSentence = "We use a dictionary to check spelling and meanings.",
                    exampleTranslation = "আমরা বানান এবং অর্থ পরীক্ষা করতে একটি অভিধান ব্যবহার করি।",
                    synonyms = "wordbook, lexicon, glossary, vocabulary",
                    antonyms = "unstructured text",
                    language = "en_to_bn",
                    searchCount = 5 // some initial popularity
                ),
                DictionaryWord(
                    word = "language",
                    translation = "ভাষা / প্রকাশভঙ্গি",
                    pronunciation = "/ˈlæŋɡwɪdʒ/ (ল্যাঙ্গুয়েজ)",
                    partOfSpeech = "Noun",
                    definition = "the system of communication in speech and writing used by people of a particular country or area.",
                    exampleSentence = "Bengali is one of the sweetest languages in the world.",
                    exampleTranslation = "বাংলা পৃথিবীর সবচেয়ে মিষ্টি ভাষাগুলোর একটি।",
                    synonyms = "tongue, speech, dialect, vernacular",
                    antonyms = "silence, muteness",
                    language = "en_to_bn"
                ),
                DictionaryWord(
                    word = "database",
                    translation = "তথ্যভাণ্ডার / ডাটাবেস",
                    pronunciation = "/ˈdeɪtəbeɪs/ (ডাটাবেস)",
                    partOfSpeech = "Noun",
                    definition = "a structured set of data held in a computer, especially one that is accessible in various ways.",
                    exampleSentence = "This dictionary app saves words in an offline database.",
                    exampleTranslation = "এই ডিকশনারি অ্যাপটি শব্দগুলোকে একটি অফলাইন ডাটাবেসে সেভ করে।",
                    synonyms = "data bank, repository, record system",
                    antonyms = "",
                    language = "en_to_bn"
                ),
                DictionaryWord(
                    word = "beautiful",
                    translation = "সুন্দর / চমৎকার",
                    pronunciation = "/ˈbjuːtɪfl/ (বিউটিফুল)",
                    partOfSpeech = "Adjective",
                    definition = "pleasing the senses or mind aesthetically.",
                    exampleSentence = "The sunset over the Ganges was beautiful.",
                    exampleTranslation = "গঙ্গার ওপর সূর্যাস্ত দেখতে চমৎকার ছিল।",
                    synonyms = "gorgeous, handsome, stunning, pretty, lovely",
                    antonyms = "ugly, hideous, repulsive",
                    language = "en_to_bn"
                ),
                DictionaryWord(
                    word = "knowledge",
                    translation = "জ্ঞান / বিদ্যা",
                    pronunciation = "/ˈnɑːlɪdʒ/ (নলেজ)",
                    partOfSpeech = "Noun",
                    definition = "facts, information, and skills acquired through experience or education; the theoretical or practical understanding of a subject.",
                    exampleSentence = "Knowledge behaves like water, flowing to the lowest heights of humility.",
                    exampleTranslation = "জ্ঞান পানির মতো আচরণ করে, যা বিনয়ের সর্বনিম্ন উচ্চতায় প্রবাহিত হয়।",
                    synonyms = "wisdom, understanding, intelligence, expertise",
                    antonyms = "ignorance, illiteracy",
                    language = "en_to_bn"
                ),
                DictionaryWord(
                    word = "teacher",
                    translation = "শিক্ষক / শিক্ষিকা",
                    pronunciation = "/ˈtiːtʃər/ (টিচার)",
                    partOfSpeech = "Noun",
                    definition = "a person who teaches, especially in a school.",
                    exampleSentence = "The teacher inspired the students to explore and ask questions.",
                    exampleTranslation = "শিক্ষক শিক্ষার্থীদের অন্বেষণ করতে এবং প্রশ্ন করতে অনুপ্রাণিত করেছিলেন।",
                    synonyms = "instructor, tutor, educator, mentor, coach",
                    antonyms = "student, pupil, disciple",
                    language = "en_to_bn"
                ),
                DictionaryWord(
                    word = "friend",
                    translation = "বন্ধু / সখা",
                    pronunciation = "/frend/ (ফ্রেন্ড)",
                    partOfSpeech = "Noun",
                    definition = "a person whom one knows and with whom one has a bond of mutual affection, typically exclusive of sexual or family relations.",
                    exampleSentence = "A true friend is always there of comfort when times are tough.",
                    exampleTranslation = "কঠিন সময়ে একজন সত্যিকারের বন্ধু সবসময় সান্ত্বনা দিতে পাশে থাকে।",
                    synonyms = "companion, buddy, mate, ally, pal",
                    antonyms = "enemy, foe, adversary",
                    language = "en_to_bn"
                ),
                DictionaryWord(
                    word = "water",
                    translation = "পানি / জল",
                    pronunciation = "/ˈwɔːtər/ (ওয়াটার)",
                    partOfSpeech = "Noun",
                    definition = "a colorless, transparent, odorless liquid that forms the seas, lakes, rivers, and rain and is the basis of the fluids of living organisms.",
                    exampleSentence = "Water is essential for all living creatures.",
                    exampleTranslation = "পানি সকল জীবন্ত প্রাণীর জন্য অপরিহার্য।",
                    synonyms = "liquid, aqua, fluid, hydration",
                    antonyms = "dryness, drought",
                    language = "en_to_bn"
                ),
                DictionaryWord(
                    word = "freedom",
                    translation = "স্বাধীনতা / মুক্তি / স্বাধিকার",
                    pronunciation = "/ˈfriːdəm/ (ফ্রিডম)",
                    partOfSpeech = "Noun",
                    definition = "the power or right to act, speak, or think as one wants without hindrance or restraint.",
                    exampleSentence = "Bangladesh gained its freedom in nineteen seventy-one after a bloody war.",
                    exampleTranslation = "রক্তক্ষয়ী যুদ্ধের পর ১৯৭১ সালে বাংলাদেশ স্বাধীনতা লাভ করে।",
                    synonyms = "liberty, independence, emancipation, release",
                    antonyms = "slavery, captivity, oppression",
                    language = "en_to_bn"
                ),
                DictionaryWord(
                    word = "love",
                    translation = "ভালোবাসা / প্রেম / স্নেহ",
                    pronunciation = "/lʌv/ (লাভ)",
                    partOfSpeech = "Noun, Verb",
                    definition = "an intense feeling of deep affection or liking for someone or something.",
                    exampleSentence = "Motherly love is unconditional and pure.",
                    exampleTranslation = "মাতৃস্নেহ নিঃশর্ত ও বিশুদ্ধ।",
                    synonyms = "affection, warmth, adoration, passion, fondness",
                    antonyms = "hate, hatred, animosity",
                    language = "en_to_bn"
                ),
                DictionaryWord(
                    word = "happy",
                    translation = "সুখী / আনন্দিত",
                    pronunciation = "/ˈhæpi/ (হ্যাপি)",
                    partOfSpeech = "Adjective",
                    definition = "feeling or showing pleasure or contentment.",
                    exampleSentence = "They were very happy to hear the good news.",
                    exampleTranslation = "সুসংবাদটি শুনে তারা খুব খুশি হয়েছিল।",
                    synonyms = "joyful, cheerful, ecstatic, glad, satisfied",
                    antonyms = "sad, unhappy, miserable, sorrowful",
                    language = "en_to_bn"
                ),
                DictionaryWord(
                    word = "book",
                    translation = "বই / পুস্তক",
                    pronunciation = "/bʊk/ (বুক)",
                    partOfSpeech = "Noun",
                    definition = "a written or printed work consisting of pages glued or sewn together along one side and bound in covers.",
                    exampleSentence = "Reading books expands our horizon of imagination.",
                    exampleTranslation = "বই পড়া আমাদের কল্পনার দিগন্ত প্রসারিত করে।",
                    synonyms = "volume, manual, publication, novel, paperback",
                    antonyms = "",
                    language = "en_to_bn"
                ),
                DictionaryWord(
                    word = "computer",
                    translation = "কম্পিউটার / গণকযন্ত্র",
                    pronunciation = "/kəmˈpjuːtər/ (কম্পিউটার)",
                    partOfSpeech = "Noun",
                    definition = "an electronic device for storing and processing data, typically in binary form, according to instructions given to it in a variable program.",
                    exampleSentence = "Most modern jobs require basic computer literacy.",
                    exampleTranslation = "অধিকাংশ আধুনিক চাকরির জন্য মৌলিক কম্পিউটার জ্ঞান প্রয়োজন।",
                    synonyms = "processor, PC, laptop, calculating machine",
                    antonyms = "",
                    language = "en_to_bn"
                ),
                DictionaryWord(
                    word = "success",
                    translation = "সাফল্য / কৃতকার্যতা",
                    pronunciation = "/səkˈses/ (সাকসেস)",
                    partOfSpeech = "Noun",
                    definition = "the accomplishment of an aim or purpose.",
                    exampleSentence = "Hard work is the primary key to achieving success.",
                    exampleTranslation = "কঠোর পরিশ্রম সাফল্য অর্জনের প্রধান চাবিকাঠি।",
                    synonyms = "triumph, victory, achievement, prosperity",
                    antonyms = "failure, defeat, loss",
                    language = "en_to_bn"
                ),

                // ---- BANGLA TO ENGLISH ----
                DictionaryWord(
                    word = "অভিধান",
                    translation = "Dictionary",
                    pronunciation = "Abhidhan (Ovidhān)",
                    partOfSpeech = "Noun (বিশেষ্য)",
                    definition = "শব্দসমূহের অর্থ, উচ্চারণ, ব্যুৎপত্তি ইত্যাদি সংগ্রাহক গ্রন্থ।",
                    exampleSentence = "সহজে অর্থ খুঁজে পেতে নতুন বাংলা অভিধানটি ব্যবহার করুন।",
                    exampleTranslation = "Use the new Bengali dictionary to find meanings easily.",
                    synonyms = "শব্দকোষ, শব্দার্থপুস্তক, অভিধানমালা",
                    antonyms = "",
                    language = "bn_to_en"
                ),
                DictionaryWord(
                    word = "ভাষা",
                    translation = "Language / Tongue",
                    pronunciation = "Bhasha (Bhāṣā)",
                    partOfSpeech = "Noun (বিশেষ্য)",
                    definition = "মনের ভাব প্রকাশের জন্য কণ্ঠনিঃসৃত সুসংবদ্ধ ও অর্থবোধক ধ্বনিসমষ্টি।",
                    exampleSentence = "বাংলা আমাদের মাতৃভাষা, যার মর্যাদা রক্ষায় শহীদরা জীবন দিয়েছিলেন।",
                    exampleTranslation = "Bengali is our mother tongue, for the dignity of which martyrs laid down their lives.",
                    synonyms = "বুলি, জবান, কথন, বাক",
                    antonyms = "মৌনতা (silence)",
                    language = "bn_to_en"
                ),
                DictionaryWord(
                    word = "বন্ধু",
                    translation = "Friend / Companion",
                    pronunciation = "Bondhu (Bondhu)",
                    partOfSpeech = "Noun (বিশেষ্য)",
                    definition = "পরস্পর সম্প্রীতির বন্ধনে যুক্ত আত্মীয় ভিন্ন অন্য কোনো ব্যক্তি।",
                    exampleSentence = "বিপদের বন্ধুই প্রকৃত বন্ধু।",
                    exampleTranslation = "A friend in need is a friend indeed.",
                    synonyms = "সখা, মিত্র, সহচর, দোস্ত, ইয়ার",
                    antonyms = "শত্রু (enemy), বৈরী (foe)",
                    language = "bn_to_en"
                ),
                DictionaryWord(
                    word = "আকাশ",
                    translation = "Sky / Firmament",
                    pronunciation = "Akash (Ākāś)",
                    partOfSpeech = "Noun (বিশেষ্য)",
                    definition = "পৃথিবীর ঊর্ধ্বস্থিত বায়ুমণ্ডল ও মহাশূন্য যা মেঘ ও নক্ষত্রে সুশোভিত থাকে।",
                    exampleSentence = "শরতের নীল আকাশ দেখতে চমৎকার লাগে।",
                    exampleTranslation = "The autumn blue sky looks wonderful.",
                    synonyms = "গগন, নভোমণ্ডল, অম্বর, আসমান",
                    antonyms = "পাতাল (underworld)",
                    language = "bn_to_en"
                ),
                DictionaryWord(
                    word = "সুন্দর",
                    translation = "Beautiful / Prepossessing / Elegant",
                    pronunciation = "Shundor (Sundar)",
                    partOfSpeech = "Adjective (বিশেষণ)",
                    definition = "যা দেখনে বা শ্রবণে মনোরম ও তৃপ্তিদায়ক।",
                    exampleSentence = "বাংলাদেশ একটি সুন্দর ও নদীমাতৃক দেশ।",
                    exampleTranslation = "Bangladesh is a beautiful and riverine country.",
                    synonyms = "মনোহর, রূপবান, চমৎকার, ললিত, নয়নকাড়া",
                    antonyms = "কুৎসিত (ugly), কদর্য (hideous)",
                    language = "bn_to_en"
                ),
                DictionaryWord(
                    word = "জ্ঞান",
                    translation = "Knowledge / Wisdom / Intelligence",
                    pronunciation = "Ggyan (Jñāna)",
                    partOfSpeech = "Noun (বিশেষ্য)",
                    definition = "কোনো বিষয় সম্পর্কে বাস্তব অভিজ্ঞতা কিংবা অধয়নজনিত লব্ধ ধারণা।",
                    exampleSentence = "জ্ঞান মানুষের শ্রেষ্ঠ সম্পদ।",
                    exampleTranslation = "Knowledge is the greatest wealth of human beings.",
                    synonyms = "প্রজ্ঞা, বিদ্যা, বোধ, বুদ্ধি",
                    antonyms = "অজ্ঞতা (ignorance)",
                    language = "bn_to_en"
                ),
                DictionaryWord(
                    word = "ভালোবাসা",
                    translation = "Love / Affection",
                    pronunciation = "Bhalobasha (Bhālobāsā)",
                    partOfSpeech = "Noun, Verb (বিশেষ্য ও ক্রিয়া)",
                    definition = "কারো প্রতি অত্যন্ত গভীর আকর্ষণ, স্নেহ ও আত্মিক টান।",
                    exampleSentence = "মাতৃভূমির প্রতি ভালোবাসাকে গভীর দেশপ্রেম বলে।",
                    exampleTranslation = "Love for the motherland is called deep patriotism.",
                    synonyms = "স্নেহ, প্রেম, প্রণয়, অনুরাগ, আদর",
                    antonyms = "ঘৃণা (hatred), বিদ্বেষ (animosity)",
                    language = "bn_to_en"
                ),
                DictionaryWord(
                    word = "ভাত",
                    translation = "Boiled Rice",
                    pronunciation = "Bhat (Bhāt)",
                    partOfSpeech = "Noun (বিশেষ্য)",
                    definition = "সিদ্ধ চাল দ্বারা প্রস্তুত বাঙালির প্রধান খাদ্য।",
                    exampleSentence = "আমরা প্রতিদিন দুপুরের খাবারে ভাত ও মাছ খাই।",
                    exampleTranslation = "We eat boiled rice and fish for lunch every day.",
                    synonyms = "অন্ন",
                    antonyms = "",
                    language = "bn_to_en"
                )
            )
            wordDao.insertWords(initialWords)
        }
    }
}
