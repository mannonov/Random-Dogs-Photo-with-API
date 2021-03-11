package uz.uzdroid.randomdogsphotowithapi

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.uzdroid.randomdogsphotowithapi.databinding.ActivityMainBinding
import uz.uzdroid.randomdogsphotowithapi.service.ApiRequest
import uz.uzdroid.randomdogsphotowithapi.service.BASE_URL

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Telefonni light modeda ushlab turadi
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Orqa fon animatsiyasini boshlaydi
        backgroundAnimation()

        // Ilova ishga tushishi bilanoq API so'rovini yuboradi
        makeApiRequest()

        binding.floatingActionButton.setOnClickListener {

            // FAB-ni aylantirish animatsiyasi
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