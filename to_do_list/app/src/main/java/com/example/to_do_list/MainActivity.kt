class MainActivity : AppCompatActivity() {
    private lateinit var quoteTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        quoteTextView = findViewById(R.id.quoteTextView)
        val shareButton: Button = findViewById(R.id.shareButton)
        val favoriteButton: Button = findViewById(R.id.favoriteButton)
        val viewFavoritesButton: Button = findViewById(R.id.viewFavoritesButton)

        // Display a random quote
        val randomQuote = quotes.random()
        quoteTextView.text = randomQuote.text

        // Share quote
        shareButton.setOnClickListener {
            shareQuote(randomQuote.text)
        }

        // Add to favorites
        favoriteButton.setOnClickListener {
            addToFavorites(randomQuote)
        }

        // View favorites
        viewFavoritesButton.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }
    }

    private fun shareQuote(quote: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, quote)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, "Share quote via"))
    }

    private fun addToFavorites(quote: Quote) {
        // Implement favorite functionality (e.g., save to database or shared preferences)
    }
}
class QuoteWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        // Fetch a new quote and save it (use SharedPreferences or database)
        val randomQuote = quotes.random()
        val sharedPref = applicationContext.getSharedPreferences("QuotePrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("dailyQuote", randomQuote.text)
            apply()
        }
        return Result.success()
    }
}
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dailyWorkRequest = PeriodicWorkRequestBuilder<QuoteWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "QuoteWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            dailyWorkRequest
        )

        // Get the daily quote from SharedPreferences
        val sharedPref = getSharedPreferences("QuotePrefs", Context.MODE_PRIVATE)
        val dailyQuote = sharedPref.getString("dailyQuote", quotes.random().text)
        quoteTextView.text = dailyQuote
    }
}

