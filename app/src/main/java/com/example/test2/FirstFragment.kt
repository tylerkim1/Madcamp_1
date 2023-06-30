package com.example.test2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.widget.AdapterView
import com.example.test2.databinding.FragmentFirstBinding
import org.json.JSONArray
import java.io.IOException

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val inputStream = requireContext().assets.open("profile.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, Charsets.UTF_8)

            val jsonArray = JSONArray(json)

            val imageId = ArrayList<Int>()
            val name = ArrayList<String>()
            val phoneNo = ArrayList<String>()
            val info = ArrayList<String>()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val imageName = jsonObject.getString("imageName")
                imageId.add(resources.getIdentifier(imageName, "drawable", requireContext().packageName))
                name.add(jsonObject.getString("name"))
                phoneNo.add(jsonObject.getString("phoneNo"))
                info.add(jsonObject.getString("info"))
            }

            val userArrayList = ArrayList<User>()

            for (i in imageId.indices) {
                val user = User(name[i], phoneNo[i], info[i], imageId[i])
                userArrayList.add(user)
            }

            val listAdapter = ListAdapter(requireContext(), userArrayList)

            binding.listview.adapter = listAdapter
            binding.listview.isClickable = true
            binding.listview.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    val intent = Intent(requireContext(), UserActivity::class.java)
                    intent.putExtra("name", name[position])
                    intent.putExtra("phone", phoneNo[position])
                    intent.putExtra("info", info[position])
                    intent.putExtra("imageid", imageId[position])
                    startActivity(intent)
                }
        } catch (e: IOException) {
            // 파일 읽기 오류 처리
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}