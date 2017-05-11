package edu.pitt.dbmi.odie.gate.pr.indexfinder;

public class TestStringToLongConversion {
	
	protected static final byte[] Hexhars = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
				'f' };


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long testValue = Long.parseLong("FFE", 16) ;
		System.out.println(testValue) ;
		System.out.println(Long.MAX_VALUE) ;
		String uuid_001 = "b425a692-6dfe-11dd-a164-75b1a02c6322";
		String uuid_002 = "b420c491-6dfe-11dd-a164-75b1a02c6322";
        String hexUuid_001 = encode(uuid_001.getBytes()) ;
        System.out.println(hexUuid_001) ;
        String hexUuid_002 = encode(uuid_002.getBytes()) ;
        System.out.println(hexUuid_002) ;
        
        Long longUuid_002 = Long.parseLong(hexUuid_002, 16) ;
        System.out.println(longUuid_002) ;
	}

	
	public static String encode(byte[] b) {

		StringBuilder s = new StringBuilder(2 * b.length);

		for (int i = 0; i < b.length; i++) {

			int v = b[i] & 0xff;
			s.append((char) Hexhars[v >> 4]);
			s.append((char) Hexhars[v & 0xf]);
		}

		return s.toString();
	}

}
