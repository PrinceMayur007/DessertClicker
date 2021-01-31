package com.example.android.dessert_clicker

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import com.example.android.dessert_clicker.databinding.ActivityMainBinding

//log tag
const val TAG = "Main Activity"

// use this to save and retrieve data from onSaveInstanceState
const val KEY_REVENUE = "revenue_key"
const val KEY_DESSERT_SOLD = "dessert_sold_key"

class MainActivity : AppCompatActivity() {

    private var revenue = 0
    private var dessertsSold = 0

    // Contains all the views
    private lateinit var binding: ActivityMainBinding

    /** Dessert Data **/

    /**
     * Simple data class that represents a dessert. Includes the resource id integer associated with
     * the image, the price it's sold for, and the startProductionAmount, which determines when
     * the dessert starts to be produced.
     */
    data class Dessert(val imageId: Int, val price: Int, val startProductionAmount: Int)

    // Create a list of all desserts, in order of when they start being produced
    private val allDesserts = listOf(
            Dessert(R.drawable.cupcake, 5, 0),
            Dessert(R.drawable.donut, 10, 5),
            Dessert(R.drawable.eclair, 15, 20),
            Dessert(R.drawable.froyo, 30, 50),
            Dessert(R.drawable.gingerbread, 50, 100),
            Dessert(R.drawable.honeycomb, 100, 200),
            Dessert(R.drawable.icecreamsandwich, 500, 500),
            Dessert(R.drawable.jellybean, 1000, 1000),
            Dessert(R.drawable.kitkat, 2000, 2000),
            Dessert(R.drawable.lollipop, 3000, 4000),
            Dessert(R.drawable.marshmallow, 4000, 8000),
            Dessert(R.drawable.nougat, 5000, 16000),
            Dessert(R.drawable.oreo, 6000, 20000)
    )
    private var currentDessert = allDesserts[0]

    //onCreate() to create the app.
    //go down to understand why there is savedInstanceState in onCreate method
    //This is part of android lifecycle, when we save data with saveInstanceState method that needed to be restored here.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate Called")
        /* Note: When you override the onCreate() method, you must call the superclass implementation to
        *  complete the creation of the Activity, so within it, you must immediately call super.onCreate().
        *  The same is true for other lifecycle callback methods.
        */
        // Use Data Binding to get reference to the views
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.dessertButton.setOnClickListener {
            onDessertClicked()
        }

        // If your activity was starting fresh, this Bundle in onCreate() is null.
        // So if the bundle is not null, you know you're "re-creating" the activity from a previously known point
        if (savedInstanceState != null) {
            revenue = savedInstanceState.getInt(KEY_REVENUE, 0)
            dessertsSold = savedInstanceState.getInt(KEY_DESSERT_SOLD, 0)
            //to show current dessert i.e. image after restoring data
            showCurrentDessert()
        }

        // Set the TextViews to the right values
        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Make sure the correct dessert is showing
        binding.dessertButton.setImageResource(currentDessert.imageId)
    }

    /**
     * Updates the score when the dessert is clicked. Possibly shows a new dessert.
     */
    private fun onDessertClicked() {

        // Update the score
        revenue += currentDessert.price
        dessertsSold++

        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Show the next dessert
        showCurrentDessert()
    }

    /**
     * Determine which dessert to show.
     */
    private fun showCurrentDessert() {
        var newDessert = allDesserts[0]
        for (dessert in allDesserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                newDessert = dessert
            }
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            else break
        }

        // If the new dessert is actually different than the current dessert, update the image
        if (newDessert != currentDessert) {
            currentDessert = newDessert
            binding.dessertButton.setImageResource(newDessert.imageId)
        }
    }

    /**
     * Menu methods
     */
    private fun onShare() {
        val shareIntent = ShareCompat.IntentBuilder.from(this)
                .setText(getString(R.string.share_text, dessertsSold, revenue))
                .setType("text/plain")
                .intent
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.sharing_not_available),
                    Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareMenuButton -> onShare()
        }
        return super.onOptionsItemSelected(item)
    }

    //onStart() to start it and make it visible on the screen.
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStartCalled")
    }

    //onResume() to give the activity focus and make it ready for the user to interact with it.
    //Despite the name, the onResume() method is called at startup, even if there is nothing to resume with on Start().
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume Called")
    }


    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause Called")
    }

    //when you rotate or push home button to make it invisible.
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop Called")
    }

    //when you shut down app or remove it from background i,e. recent apps
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy Called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart Called")
    }
    // This will save data when configuration change happens
    // There are two overrides for onSaveInstanceState(), one with just an outState parameter,
    // and one that includes outState and outPersistentState parameters.

    //A Bundle is a collection of key-value pairs, where the keys are always strings.
    // You can put simple data, such as Int and Boolean values, into the bundle.
    //Always put only small data in bundle, otherwise crash happens
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_REVENUE, revenue)
        //saved with bundle outState putted revenue value in const created at top level
        outState.putInt(KEY_DESSERT_SOLD, dessertsSold)
        //saved with bundle outState putted dessertSold value in const created at top level
        //now this values had to be restored, means to be given to onCreate() when configuration changed,
        // The Activity state can be restored in onCreate(Bundle)
        // or onRestoreInstanceState(Bundle) (the Bundle populated by onSaveInstanceState() method will be passed
        // to both lifecycle callback methods).
        Log.d(TAG, "onSaveInstanceState Called")
    }
}
