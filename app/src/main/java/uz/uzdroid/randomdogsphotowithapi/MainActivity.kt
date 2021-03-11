package uz.uzdroid.randomdogsphotowithapi

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.uzdroid.randomdogsphotowithapi.service.ApiRequest
import uz.uzdroid.randomdogsphotowithapi.service.BASE_URL
import uz.uzdroid.randomdogsphotowithapi.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Keeps the phone in light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Starts up the background transitions
        backgroundAnimation()

        // Makes an API request as soon as the app starts
        makeApiRequest()

        binding.floatingActionButton.setOnClickListener {

            // FAB rotate animation
            binding.floatingActionButton.animate().apply {
                rotationBy(360f)
                duration = 1000
            }.start()

            makeApiRequest()
            binding.ivRandomDog.visibility = View.GONE

        }
    }

    private fun backgroundAnimation() {
        val animatonDrawable: AnimationDrawable = binding.rlLayout.background as AnimationDrawable
        animatonDrawable.apply {
            setEnterFadeDuration(1000)
            setExitFadeDuration(3000)
            start()
        }
    }

    private fun makeApiRequest() {
        val api = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiRequest::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getRandomDog()
                Log.d("Main", "Size: ${response.fileSizeBytes}")

                //Agar rasm taxminan 0,4 mb dan kam bo'lsa, biz uni o'z dasturimizga yuklashga harakat qilamiz, aks holda yana urinib ko'ramiz.
                if (response.fileSizeBytes < 400_000) {
                    withContext(Dispatchers.Main) {
                        Glide.with(applicationContext).load(response.url).into(binding.ivRandomDog)
                        binding.ivRandomDog.visibility = View.VISIBLE
                    }
                } else {
                    makeApiRequest()
                }

            } catch (e: Exception) {
                Log.e("Main", "Error: ${e.message}")
            }
        }
    }

}