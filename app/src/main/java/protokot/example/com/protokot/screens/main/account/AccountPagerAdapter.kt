package protokot.example.com.protokot.screens.main.account

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import protokot.example.com.protokot.data.UserProfile

/**
 * Created by Cardiweb on 24/01/2018.
 */
class AccountPagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return ProfileEditionFragment.newInstance();
            1 -> return ProfileBooksFragment.newInstance();
        }
        return AccountFragment.newInstance()
    }

    override fun getCount() = 2

    override fun getPageTitle(position: Int): CharSequence {
        when(position) {
            0 -> return "Informations";
            1 -> return "Mes livres";
        }
        return "Informations"
    }

}