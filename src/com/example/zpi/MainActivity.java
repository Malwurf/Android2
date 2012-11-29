package com.example.zpi;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.example.zpi.communication.*;
import com.google.android.gcm.GCMRegistrar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements ResponseListener {
	ToggleButton brama;
	Button woda;
	ToggleButton alarm;
	Button rolety;
	Button ogrod;
	Button status;
	Button koniec;
	Button harmon;
    EditText haslo;
    Boolean statusAlarm;
    Connect c;
    Dialog sk;
    protected AccountManager accountManager;

    final private static String SENDER_ID = "303941619301";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        c=new Connect(this);
        c.addResponseListener(this);
        try {
            c.requestGet(2,0);
            c.requestGet(3,0);
        } catch (ServerErrorException e) {
            e.printStackTrace();
            problemSerwer();
            Log.d("serwer","brak");
        } catch (NoInternetException e) {
            e.printStackTrace();
            brakInternetu();
            //To change body of catch statement use File | Settings | File Templates.
        }
        accountManager = AccountManager.get(getApplicationContext());
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Log.d("cos",""+accounts[0]);
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
          GCMRegistrar.register(this, SENDER_ID);
        } 
        
        addListenerOnButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    public void addListenerOnButton(){
    	brama= (ToggleButton) findViewById(R.id.brama);
    	woda=(Button)findViewById(R.id.woda);
    	alarm= (ToggleButton) findViewById(R.id.alarm);
    	rolety=(Button)findViewById(R.id.rolety);
    	ogrod=(Button)findViewById(R.id.ogrod);
    	status=(Button)findViewById(R.id.status);
    	koniec=(Button)findViewById(R.id.koniec);
    	harmon=(Button)findViewById(R.id.harmonogram);
    	brama.setOnClickListener(new OnClickListener() {
    		
			
			public void onClick(View arg0) {
			if(brama.isChecked()){
                try {
                    c.requestSet(2,0,"true");
                    Log.d("alarm",""+alarm.isChecked());
                } catch (ServerErrorException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (NoInternetException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
                else{
                try {
                    c.requestSet(2,0,"false");
                } catch (ServerErrorException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (NoInternetException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
			}
		});
    	woda.setOnClickListener(new OnClickListener() {
    		
			
			public void onClick(View arg0) {
			Intent i=new Intent(getApplicationContext(),Woda.class);
			startActivity(i);
			}
		});

    	alarm.setOnClickListener(new OnClickListener() {
    		
			
			public void onClick(View arg0) {
                alarm.toggle();
                onCreateDialog();
			//Intent i=new Intent(getApplicationContext(),Alarm.class);
			//startActivity(i);
			}
		});
    	rolety.setOnClickListener(new OnClickListener() {
    		
			
			public void onClick(View arg0) {
			Intent i=new Intent(getApplicationContext(),Rolety.class);
			startActivity(i);
			}
		});
    	ogrod.setOnClickListener(new OnClickListener() {
    		
			
			public void onClick(View arg0) {
			Intent i=new Intent(getApplicationContext(),Ogrod.class);
			startActivity(i);
			}
		});
    	status.setOnClickListener(new OnClickListener() {
    		
			
			public void onClick(View arg0) {
			Intent i=new Intent(getApplicationContext(),Status.class);
			startActivity(i);
			}
		});
    	koniec.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
			finish();
			}
		});
    	harmon.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
			Intent i=new Intent(getApplicationContext(),Harmonogramy.class);
			startActivity(i);
			}
		});
    }
    protected void onCreateDialog() {

         sk=new Dialog(this);
        LayoutInflater inflater=(LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout=inflater.inflate(R.layout.alarm_dialog, (ViewGroup)findViewById(R.id.dialog_lay_alr));
        haslo=(EditText)layout.findViewById(R.id.alrHaslDial);
        haslo.setInputType(InputType.TYPE_CLASS_NUMBER);
        haslo.setTransformationMethod(PasswordTransformationMethod.getInstance());
            sk.setContentView(layout);
            sk.setTitle("Wprowadz PIN");
            Button ok=(Button)layout.findViewById(R.id.alrOkDial);
            Button anl=(Button)layout.findViewById(R.id.alrAnlDial);
            ok.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    try {
                        c.requestGet(3,1);
                    } catch (ServerErrorException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (NoInternetException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }});

            anl.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    sk.cancel();
                }});
            sk.show();
    }
    protected void brakInternetu(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Brak połączenia z internetem");
        builder.setMessage("Aby korzystać z aplikacji wymagane jest połączenie z internetem. " +
                "Uruchom aplikacje gdy nawiązane zostanie połączenie internetowe");
        builder.setPositiveButton("OK", new Dialog.OnClickListener()

        {
              @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                 finish();
            }
        });
        builder.create().show();
    }
    protected void problemSerwer(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Brak połączenia z serwerem");
        builder.setMessage("Wynikł problem połączenia z serwerem systemu. Zrestartuj serwer i spróbuj ponownie");
        builder.setPositiveButton("OK", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });
        builder.create().show();
    }

    @Override
    public void processResponse(Response res) {
        if(res.isERROR())   {
            Log.d("err","dkjashdj");
            return;
        }
        if(res.getType()==Response.GET){
            if(res.getModule()==2){
                Boolean statusBramy = Boolean.valueOf(res.getValue());
                brama.setChecked(statusBramy);
            }
            else
            if(res.getModule()==3){
                if(res.getPort()==0){
                    statusAlarm = Integer.parseInt(res.getValue())>0;
                    alarm.setChecked(statusAlarm);
                }
                else{
                    if(res.getValue().equals(haslo.getText().toString())){
                            try {
                                c.requestSet(3,0,""+(statusAlarm?0:1));
                                sk.cancel();
                            } catch (ServerErrorException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            } catch (NoInternetException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Hasło nieprawidłowe, spróbuj ponownie", Toast.LENGTH_LONG).show();
                    }
                }

            }

        }
        else {
            if(res.getModule()==3)
                alarm.setChecked(statusAlarm = !statusAlarm);
        }
    }
}
