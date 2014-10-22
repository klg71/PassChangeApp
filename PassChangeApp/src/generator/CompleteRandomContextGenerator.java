package generator;

import java.util.Date;
import java.util.Random;


public class CompleteRandomContextGenerator extends ContextPasswordGenerator{

	@Override
	public String generatePassword(int length) {
		byte[] bytes=new byte[15];
		Random random=new Random();
		Float randomFloat=random.nextFloat()*random.nextFloat()+random.nextInt();
		for(int i=0;i<length;i++){
			bytes[i]=(byte) ((byte) random.nextInt(93)+33);
		}
//		Encoder encoder = Base64.getEncoder();
//		String key = new String(encoder.encode(new String(bytes).getBytes()));
		return new String(bytes);
	}
	
}
