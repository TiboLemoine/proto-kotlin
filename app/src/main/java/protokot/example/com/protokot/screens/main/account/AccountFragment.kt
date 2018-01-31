package protokot.example.com.protokot.screens.main.account

import android.os.Bundle
import android.view.View
import protokot.example.com.protokot.R
import protokot.example.com.protokot.screens.base.AbstractFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import protokot.example.com.protokot.data.UserProfile
import protokot.example.com.protokot.screens.main.booklist.BookSummary

/**
 * Created by Cardiweb on 24/01/2018.
 */
class AccountFragment : AbstractFragment() {

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }

    override fun getContentView() = R.layout.fragment_profile

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName.text = "John Doe"

        pager.adapter = AccountPagerAdapter(fragmentManager)
        tab.setupWithViewPager(pager)
    }
}