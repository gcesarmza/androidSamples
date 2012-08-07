package com.gustavogenovese.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CalculatorActivity extends Activity implements OnClickListener{

    private static String TAG = "calculator";
    private CalculatorModel model;
    private Button equalsButton;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
        setContentView(R.layout.calculator);
        model = new CalculatorModel();
        equalsButton = (Button)findViewById(R.id.button_equals);
        equalsButton.setOnClickListener(this);
    }
    
    public void digitClicked(View button){
    	String tag = button.getTag().toString();
    	try {
    		int digit = Integer.parseInt(tag);
    		model.digitPressed(digit);
    		refreshDisplay();
    	}catch (NumberFormatException ex){}
    }
    
    public void operClicked(View button){
    	String tag = button.getTag().toString();
    	try {
    		int oper = Integer.parseInt(tag);
    		model.setOperation(oper);
    		refreshDisplay();
    	}catch (NumberFormatException ex){}    	
    }
    
    private void refreshDisplay(){
    	TextView display = (TextView)findViewById(R.id.calculatorDisplay);
    	display.setText(model.getBuffer());
    }

	@Override
	public void onClick(View v) {
		//equals button pressed
		model.oper();
		refreshDisplay();
	}

}

