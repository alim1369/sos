package sos.police_v2;

 import sos.base.SOSAgent;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;
import sos.police_v2.base.AbstractPoliceOfficeActivity;

/**
 * SOS center agent.
 */
public class PoliceOfficeActivity extends AbstractPoliceOfficeActivity {
	
	public PoliceOfficeActivity(SOSAgent<?> sosAgent) {
		super(sosAgent);
	}
	
	@Override
	protected void preCompute() {
		super.preCompute();
	}
	
	@Override
	protected void prepareForThink() throws Exception {
		super.prepareForThink();
	}
	
	@Override
	protected void think() throws SOSActionException, Exception {
		super.think();
	}
	
	@Override
	protected void finalizeThink() throws Exception {
		super.finalizeThink();
	}
	
	/*
	 * Ali: Please keep it at the end!!!!(non-Javadoc)
	 */
	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		super.hear(header, data, dynamicBitArray,sender, channel);
	}
}