package com.gustavogenovese.calculator;

public class CalculatorModel {
	private int tempBuffer;
	private int operationBuffer;
	private int operation;
	
	public void digitPressed(int digit){
		if (digit<0 || digit >9)
			return;
		tempBuffer = 10*tempBuffer + digit;
	}
	
	public void setOperation(int operation){
		//0 = +
		//1 = -
		//2 = *
		//3 = /
		if (operation<0 || operation>3)
			return;
		this.operation = operation;
		operationBuffer = tempBuffer;
		tempBuffer = 0;
	}
	
	public void oper(){
		
		switch (operation){
		case 0:
			tempBuffer += operationBuffer; 
			break;
			
		case 1:
			tempBuffer -= operationBuffer;
			break;
			
		case 2:
			tempBuffer *= operationBuffer;
			break;
			
		case 3:
			tempBuffer /= operationBuffer;
			break;
		}
	}
	
	public String getBuffer(){
		return Integer.toString(tempBuffer);
	}
}
