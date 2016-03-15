package com.um.dao;

import com.mathworks.toolbox.javabuilder.MWException;

import newpredictum.Predictum;

public class PredictumBean {
	
	private Predictum predictum;

	public Predictum getPredictum() {
		return predictum;
	}

	public void setPredictum(Predictum predictum) {
		this.predictum = predictum;
	}
	
	public PredictumBean() throws MWException {
		predictum = new Predictum();
	}
}
