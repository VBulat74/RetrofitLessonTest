package com.example.retrofitlesson

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.retrofitlesson.databinding.FragmentLoginBinding
import com.example.retrofitlesson.retrofit.AuthRequest
import com.example.retrofitlesson.retrofit.MainApi
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by activityViewModels()
    private lateinit var mainApi: MainApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRetrofit()

        binding.apply {
            btNext.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_productsFragment)

            }

            btSigIn.setOnClickListener {
                auth(
                    AuthRequest(
                        binding.edLogin.text.toString(),
                        binding.edPassword.text.toString(),
                    )

                )
            }
        }

        binding.btNext.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_productsFragment)
        }
    }

    private fun initRetrofit(){
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient
            .Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit
            .Builder()
            .baseUrl("https://dummyjson.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mainApi = retrofit.create(MainApi::class.java)
    }

    private fun auth(autoRequest: AuthRequest){
        CoroutineScope (Dispatchers.IO).launch {
            val response = mainApi.auth(autoRequest)
            val message = response.errorBody()?.string()?.let {
                JSONObject(it).getString("message")
            }
            requireActivity().runOnUiThread {
                if(response.isSuccessful){
                    val user = response.body()
                    if(user != null){
                        Picasso.get().load(user.image).into(binding.imageView)
                        binding.tvName.text = user.firstName
                        binding.btNext.visibility = View.VISIBLE
                        binding.tvError.visibility  = View.INVISIBLE
                        viewModel.token.value = user.token
                    }

                }else{
                    binding.tvError.text = message
                    binding.tvError.visibility  = View.VISIBLE
                }

            }
        }
    }
}