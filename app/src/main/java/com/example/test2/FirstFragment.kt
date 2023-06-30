package com.example.test2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.example.test2.databinding.ActivityMainBinding
import com.example.test2.databinding.FragmentFirstBinding

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

        val imageId = intArrayOf(
            R.drawable.person,
            R.drawable.person,
            R.drawable.person,
            R.drawable.person,
            R.drawable.person,
            R.drawable.person,
            R.drawable.person,
            R.drawable.person,
            R.drawable.person
        )
        val name = arrayOf(
            "김태형", "김태형", "김태형", "김태형", "김태형",
            "김태형", "김태형", "김태형", "김태형"
        )
        val phoneNo = arrayOf(
            "010-0000-0000", "010-0000-0000", "010-0000-0000", "010-0000-0000", "010-0000-0000",
            "010-0000-0000", "010-0000-0000", "010-0000-0000", "010-0000-0000"
        )
        val info = arrayOf(
            "KAIST 19학번", "KAIST 19학번", "KAIST 19학번", "KAIST 19학번", "KAIST 19학번",
            "KAIST 19학번", "KAIST 19학번", "KAIST 19학번", "KAIST 19학번"
        )

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}