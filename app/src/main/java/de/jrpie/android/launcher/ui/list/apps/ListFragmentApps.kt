package de.jrpie.android.launcher.ui.list.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import de.jrpie.android.launcher.databinding.ListAppsBinding
import de.jrpie.android.launcher.openSoftKeyboard
import de.jrpie.android.launcher.preferences.LauncherPreferences
import de.jrpie.android.launcher.ui.UIObject
import de.jrpie.android.launcher.ui.list.ListActivity
import de.jrpie.android.launcher.ui.list.forGesture
import de.jrpie.android.launcher.ui.list.intention


/**
 * The [ListFragmentApps] is used as a tab in ListActivity.
 *
 * It is a list of all installed applications that are can be launched.
 */
class ListFragmentApps : Fragment(), UIObject {
    private lateinit var binding: ListAppsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ListAppsBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super<Fragment>.onStart()
        super<UIObject>.onStart()
    }

    override fun setOnClicks() {}

    override fun adjustLayout() {

        val appsRViewAdapter = AppsRecyclerAdapter(requireActivity(), intention, forGesture)

        // set up the list / recycler
        binding.listAppsRview.apply {
            // improve performance (since content changes don't change the layout size)
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = appsRViewAdapter
        }

        binding.listAppsSearchview.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                appsRViewAdapter.filter(query)
                appsRViewAdapter.selectItem(0)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                appsRViewAdapter.filter(newText)
                return false
            }
        })

        if (intention == ListActivity.ListActivityIntention.VIEW
            && LauncherPreferences.functionality().searchAutoOpenKeyboard()
        ) {
            openSoftKeyboard(requireContext(), binding.listAppsSearchview)
        }
    }
}
