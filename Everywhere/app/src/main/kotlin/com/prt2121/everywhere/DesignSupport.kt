package com.prt2121.everywhere

import android.app.Activity
import android.support.design.widget.*
import android.support.v4.app.Fragment
import android.support.v4.view.PagerTabStrip
import android.support.v4.view.PagerTitleStrip
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.NestedScrollView
import android.support.v4.widget.SlidingPaneLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewManager
import android.widget.GridLayout
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.find

/* Android v4 support library */

fun ViewManager.viewPager(init: ViewPager.() -> Unit = {}) =
    ankoView({ ViewPager(it) }, init)

fun ViewManager.pagerTitleStrip(init: PagerTitleStrip.() -> Unit = {}) =
    ankoView({ PagerTitleStrip(it) }, init)

fun ViewManager.pagerTabStrip(init: PagerTabStrip.() -> Unit = {}) =
    ankoView({ PagerTabStrip(it) }, init)

fun ViewManager.drawerLayout(init: DrawerLayout.() -> Unit = {}) =
    ankoView({ DrawerLayout(it) }, init)

fun ViewManager.slidingPaneLayout(init: SlidingPaneLayout.() -> Unit = {}) =
    ankoView({ SlidingPaneLayout(it) }, init)

fun ViewManager.nestedScrollView(init: NestedScrollView.() -> Unit = {}) =
    ankoView({ NestedScrollView(it) }, init)

/* Android v7 support libraries */

fun ViewManager.toolbar(init: Toolbar.() -> Unit = {}) =
    ankoView({ Toolbar(it) }, init)

fun ViewManager.gridLayout(init: GridLayout.() -> Unit = {}) =
    ankoView({ GridLayout(it) }, init)

fun ViewManager.cardView(init: CardView.() -> Unit = {}) =
    ankoView({ CardView(it) }, init)

fun ViewManager.recyclerView(init: RecyclerView.() -> Unit = {}) =
    ankoView({ RecyclerView(it) }, init)

/* Android design support library */

fun ViewManager.appBarLayout(init: AppBarLayout.() -> Unit = {}) =
    ankoView({ AppBarLayout(it) }, init)

fun ViewManager.collapsingToolbarLayout(init: CollapsingToolbarLayout.() -> Unit = {}) =
    ankoView({ CollapsingToolbarLayout(it) }, init)

fun ViewManager.coordinatorLayout(init: CoordinatorLayout.() -> Unit = {}) =
    ankoView({ CoordinatorLayout(it) }, init)

fun ViewManager.floatingActionButton(init: FloatingActionButton.() -> Unit = {}) =
    ankoView({ FloatingActionButton(it) }, init)

fun ViewManager.navigationView(init: NavigationView.() -> Unit = {}) =
    ankoView({ NavigationView(it) }, init)

fun ViewManager.tabLayout(init: TabLayout.() -> Unit = {}) =
    ankoView({ TabLayout(it) }, init)

fun ViewManager.textInputLayout(init: TextInputLayout.() -> Unit = {}) =
    ankoView({ TextInputLayout(it) }, init)

fun View.snackbar(text: CharSequence, duration: Int = Snackbar.LENGTH_SHORT, init: Snackbar.() -> Unit = {}): Snackbar {
  val snack = Snackbar.make(this, text, duration)
  snack.init()
  snack.show()
  return snack
}

fun View.snackbar(text: Int, duration: Int = Snackbar.LENGTH_SHORT, init: Snackbar.() -> Unit = {}): Snackbar {
  val snack = Snackbar.make(this, text, duration)
  snack.init()
  snack.show()
  return snack
}

fun Fragment.snackbar(text: CharSequence, duration: Int = Snackbar.LENGTH_SHORT, init: Snackbar.() -> Unit = {}): Snackbar =
    getView().snackbar(text, duration, init)

fun Fragment.snackbar(text: Int, duration: Int = Snackbar.LENGTH_SHORT, init: Snackbar.() -> Unit = {}): Snackbar =
    getView().snackbar(text, duration, init)

fun Activity.snackbar(text: CharSequence, duration: Int = Snackbar.LENGTH_SHORT, init: Snackbar.() -> Unit = {}): Snackbar =
    find<View>(android.R.id.content).snackbar(text, duration, init)

fun Activity.snackbar(text: Int, duration: Int = Snackbar.LENGTH_SHORT, init: Snackbar.() -> Unit = {}): Snackbar =
    find<View>(android.R.id.content).snackbar(text, duration, init)