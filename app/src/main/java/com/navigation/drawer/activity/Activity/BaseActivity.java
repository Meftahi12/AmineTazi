package com.navigation.drawer.activity.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.navigation.drawer.activity.R;


public class BaseActivity extends Activity {
	ProgressDialog pDialog;
	public boolean loginVerifed = false;


	public static int iconId = R.drawable.ic_launcher;

	protected FrameLayout frameLayout;
	

	protected ListView mDrawerList;


	/*

	TALITAN


	MRE7BA B SI AMINE .. TCHREB SHI HAJA ??


	Daba Apres matcreer L'activity .. Ajouter f had listArray li l te7t .. smia b trtib li bghitiha tban fih



	 */







	protected String[] listArray = {"Acceuil", "Profil", "Editer mon Profil","Se deconnecter"};
	

	protected static int position;
	

	private static boolean isLaunch = true;
	

	private DrawerLayout mDrawerLayout;
	

	private ActionBarDrawerToggle actionBarDrawerToggle;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_drawer_base_layout);

		frameLayout = (FrameLayout)findViewById(R.id.content_frame);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		

		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, listArray));
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				openActivity(position);
			}
		});
		getActionBar().setIcon(iconId);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setIcon(iconId);
		actionBarDrawerToggle = new ActionBarDrawerToggle(
				this,						/* host Activity */
				mDrawerLayout,
				iconId/* DrawerLayout object */
				,     /* nav drawer image to replace 'Up' caret */
				R.string.open_drawer,       /* "open drawer" description for accessibility */
				R.string.close_drawer)      /* "close drawer" description for accessibility */ 
		{ 
			@Override
			public void onDrawerClosed(View drawerView) {
				getActionBar().setTitle(listArray[position]);
                invalidateOptionsMenu();
				getActionBar().setIcon(iconId);// creates call to onPrepareOptionsMenu()
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				if(Login.isLogin) {
					getActionBar().setTitle(getString(R.string.app_name));
					invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
					getActionBar().setIcon(iconId);
					super.onDrawerOpened(drawerView);
				}
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				getActionBar().setIcon(iconId);
				super.onDrawerSlide(drawerView, slideOffset);
			}

			@Override
			public void onDrawerStateChanged(int newState) {
				super.onDrawerStateChanged(newState);
			}
		};
		mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
		


		if(isLaunch){
			isLaunch = false;
			mDrawerLayout.closeDrawer(mDrawerList);
			BaseActivity.position = 0;
		}
	}
	protected void openActivity(int position) {


		mDrawerLayout.closeDrawer(mDrawerList);
		BaseActivity.position = position;

		/*

		RABI3AN


		DARORI tzid hna

		case i :
			startActivity(new Intent(this,shi7aja));
			break ;


			tel que i hia l position dial had l'action f dik l'array li lfoq .. rak 3arf pos f tableau tatbda b 0


			SAAAFI

		 */





		switch (position) {
			case 0:
				startActivity(new Intent(this, Acceuil.class));
				break;
			case 1:
				startActivity(new Intent(this, Profil.class));
				break;
			case 2:
				startActivity(new Intent(this, Editer.class));
				break;
			case 3:
				Login.isLogin = false ;
				startActivity(new Intent(this,Login.class));
				break;


			default:
			break;
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// The action bar home/up action should open or close the drawer. 
		// ActionBarDrawerToggle will take care of this.
		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		
		switch (item.getItemId()) {

		
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		getActionBar().setIcon(iconId);
        return super.onPrepareOptionsMenu(menu);
    }
    
    /* We can override onBackPressed method to toggle navigation drawer*/
	@Override
	public void onBackPressed() {
		if(mDrawerLayout.isDrawerOpen(mDrawerList)){
			mDrawerLayout.closeDrawer(mDrawerList);
		}else {
			mDrawerLayout.openDrawer(mDrawerList);
		}
	}
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
