package com.example.compteurvitesse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.*;
public class MainActivity extends Activity {
	// Declaration des variables globales 
	// GPS
	LocationManager vLocationManager;
	LocationListener vLocationListener;
	public static final double[] UNIT_MULTIPLIERS = { 0.001, 0.000621371192 };
	// Vielle ecran
	protected PowerManager.WakeLock vWakeLock;
	// sons
	Ringtone vAlerteSonore;
	// Interface graphique
	TextView vTextViewSpeed;
	ToggleButton vToogleButton_50;
	ToggleButton vToogleButton_70;
	ToggleButton vToogleButton_90;
	ToggleButton vToogleButton_110;
	ToggleButton vToogleButton_130;
	CheckBox vCheckBoxVitesseCompteur;
	CheckBox vCheckBoxAvecSon;
	ImageView vImageView_Attention;
	// variable
	double vConsigne = 0; // vitesse max en km/h
	int vCompteurAlerte = 0; // compteur d'alerte sonore
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // L'application bloque la mise en veille (voir onDestroy pour la relacher
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.vWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.vWakeLock.acquire();
        // Creation de l'alerte sonore qui est le son de notification par défaut
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		vAlerteSonore = RingtoneManager.getRingtone(getApplicationContext(), notification);
        // GPS
        vLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        vLocationListener = new mylocationlistener();
        vLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, vLocationListener);
        // Interface graphique
        vTextViewSpeed = (TextView)findViewById(R.id.textView_SPEED);
    	vToogleButton_50 = (ToggleButton)findViewById(R.id.toggleButton_50);
    	vToogleButton_70= (ToggleButton)findViewById(R.id.ToggleButton_70);
     	vToogleButton_90= (ToggleButton)findViewById(R.id.ToggleButton_90);
    	vToogleButton_110= (ToggleButton)findViewById(R.id.ToggleButton_110);
    	vToogleButton_130= (ToggleButton)findViewById(R.id.ToggleButton_130);
    	vCheckBoxVitesseCompteur= (CheckBox)findViewById(R.id.checkBox_VITESSE_COMPTEUR);
    	vCheckBoxAvecSon = (CheckBox)findViewById(R.id.checkBox_SON);
    	vImageView_Attention = (ImageView)findViewById(R.id.ImageView_ATTENTION);
    	vImageView_Attention.setImageResource(R.drawable.waiting);
    	// Evenements
    	vToogleButton_50.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
				// TODO Auto-generated method stub
    			if (vToogleButton_50.isChecked()) {
    				vConsigne = 50;
    				// deselectionne les autres tooglebuttons
    				vToogleButton_70.setChecked(false);
    				vToogleButton_90.setChecked(false);
    				vToogleButton_110.setChecked(false);
    				vToogleButton_130.setChecked(false);
    			}
			}
		});
    	vToogleButton_70.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
				// TODO Auto-generated method stub
    			if (vToogleButton_70.isChecked())
    			{
    				vConsigne = 70;
    				vToogleButton_50.setChecked(false);
    				vToogleButton_90.setChecked(false);
    				vToogleButton_110.setChecked(false);
    				vToogleButton_130.setChecked(false);
    			}
			}
		});
    	vToogleButton_90.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
				// TODO Auto-generated method stub
    			if (vToogleButton_90.isChecked())
    			{
    				vConsigne = 90;
    				vToogleButton_50.setChecked(false);
    				vToogleButton_70.setChecked(false);
    				vToogleButton_50.setChecked(false);
    				vToogleButton_110.setChecked(false);
    				vToogleButton_130.setChecked(false);
    			}
			}
		});
    	vToogleButton_110.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
				// TODO Auto-generated method stub
    			if (vToogleButton_110.isChecked())
    			{
    				vConsigne = 110;
    				vToogleButton_50.setChecked(false);
    				vToogleButton_70.setChecked(false);
    				vToogleButton_50.setChecked(false);
    				vToogleButton_90.setChecked(false);
    				vToogleButton_130.setChecked(false);
    			}
			}
		});
    	vToogleButton_130.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v)
    		{
				// TODO Auto-generated method stub
    			if (vToogleButton_130.isChecked()) {
    				vConsigne = 130;
    				vToogleButton_50.setChecked(false);
    				vToogleButton_70.setChecked(false);
    				vToogleButton_50.setChecked(false);
    				vToogleButton_90.setChecked(false);
    				vToogleButton_110.setChecked(false);
    			}
    		}
		});
    } // fin onCreate
    // Affichage et traitement de la vitesse instannée
    public void displaySpeed(double pCurrentSpeed)
    {
    	// variable pour l'arrondi
    	double vRounded =0;
    	// Si vitesse compteur selectionnée on ajoute 7% à la vitesse indiquée par le GPS
    	if (vCheckBoxVitesseCompteur.isChecked())
    	{
    		// arrondi à 1 chiffre après la virgule
    		vRounded = roundDecimal(convertSpeed(pCurrentSpeed*1.07),1);
    	}
    	else
    	{
    		vRounded = roundDecimal(convertSpeed(pCurrentSpeed),1);
    	}
    	// si la vitesse instannée est supérieure à la consigne
    	if (vRounded > vConsigne)
    	{
    		// on afffiche la vitesse en rouge
    		vTextViewSpeed.setTextColor(Color.RED);
    		// on affecte à l'imageView l'image d'un panneau Attention
    		vImageView_Attention.setImageResource(R.drawable.attention);
    		// On affiche l'image en rendant l'imageview visible
    		vImageView_Attention.setVisibility(0);
    		// Si la checkbox 'avec son" est checkée on joue le son de notification
    		if (vCheckBoxAvecSon.isChecked())
    		{
    			// on ne joue le son que 2 fois
    			if (vCompteurAlerte<3)
    			{
    				// aletre sonore audible
    				vAlerteSonore.play();
    				// incremente la valeur du compteur de nombre d'alerte sonore
    				vCompteurAlerte++;
    			}
    		}
    	}
    	else // la vitesse instannée est inferieur à la vitesse max
    	{
    		// on afffiche la vitesse en gris fonéé
    		vTextViewSpeed.setTextColor(Color.DKGRAY);
    		// on affecte à l'imageView l'image d'un panneau Attention
    		vImageView_Attention.setImageResource(R.drawable.attention);
    		// On n'affiche pas l'image en la rendant invisible
    		vImageView_Attention.setVisibility(4);
    		// on remet le compteur de nombre d'alertes sonores entendues à zéro
    		vCompteurAlerte = 0;
    	}
    	vTextViewSpeed.setText(String.valueOf(vRounded));
    }
    // fonction d'arrondie entree : le nobmre a arrondir, et le nombre
    // de chiffres après la virgule désiré
    private double roundDecimal(double value, final int decimalPlace)
    {
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
            value = bd.doubleValue();
            return value;
    }
    // conversion de la vitesse GPS en km/h
	private double convertSpeed(double pSpeed){
		return pSpeed*3.6;
}
    @Override
    // surchage de onDestroy qui est appelé quand on quitte l'application
    // ici on relache l'interdiction de mise en veille
    public void onDestroy() {
    	// on remet la veille ecran comme avant
        this.vWakeLock.release();
        super.onDestroy();
    }
    // Classe interne de gestion du GPS implementant l'interface (java) LocationListener
    // on devra donc surchager plusieurs méthodes    
    private class mylocationlistener implements LocationListener {
        // Ecouteur sur le GPS, appelé à chaque changement de position
        public void onLocationChanged(Location location)
        {
            // si une position est renvoyé par le GPS
        	if (location != null)
        	{
        		// si une vitesse est détectée
            	if (location.hasSpeed())
            	{
            		double vCurrentSpeed = location.getSpeed();
            		// appel de la méthode displaySpeed en lui
            		// passant la vitesse brute lu dans le GPS
            	    displaySpeed(vCurrentSpeed);
            	}
            }
        }
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
    } // fin class interne
}// fin class