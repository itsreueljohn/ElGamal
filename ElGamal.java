import java.math.BigInteger;
import java.util.Random;

class ElGamal{

	static BigInteger two = new BigInteger("2");
	static int bitCount=300;
	static int securityParameter=3;

	static BigInteger p;
	static BigInteger q;
	static BigInteger g;
	static BigInteger h;
	static BigInteger a;
	static BigInteger c1;
	static BigInteger c2;
	static BigInteger input;

	static boolean millerRabin(BigInteger n, int t){

		Random randomGenerator = new Random();
		int s;

		if(n.equals(two)) return true;

		if(n.compareTo(two)==-1 || (n.mod(two)==BigInteger.ZERO)){
			return false;
		}

		BigInteger temp = n.subtract(BigInteger.ONE);

		BigInteger r =temp;

		for(s=0;r.mod(two).equals(BigInteger.ZERO);s++){
			r= r.divide(two);
		}

		for(int i=0;i<t;i++){
			BigInteger a= new BigInteger(bitCount,randomGenerator);
			a=a.mod(temp.subtract(BigInteger.ONE)).add(BigInteger.ONE);// 2<=a<=n-2
			BigInteger y= modExp(a,r,n);

			if((!y.equals(BigInteger.ONE)) && (!y.equals(temp))){

				for(int j=1; j<=s-1 && (!y.equals(temp));j++){
					y=modExp(y,two,n);
					if(y.equals(BigInteger.ONE)) return false;
				}
				if(!(y.equals(temp))) return false;
			}
		}
		return true;
	}

	static BigInteger modExp(BigInteger m, BigInteger e, BigInteger n){
		BigInteger r= e.mod(two).equals(BigInteger.ONE) ? m.mod(n):BigInteger.ONE;
		e= e.divide(two);

		while(e.compareTo(BigInteger.ZERO)>0){
			m=(m.multiply(m)).mod(n);

			if(e.mod(two).equals(BigInteger.ONE)) r= (r.multiply(m)).mod(n);
			e=e.divide(two);
		}
		return r;
	}

	//Generates an odd number of bitCount number of bits and checks if prime with millerRabin test
	static BigInteger generatePrime(){

		while(true){
			Random x = new Random();
			BigInteger a = new BigInteger(bitCount,x);
			if(a.mod(two).equals(BigInteger.ZERO)) a= a.add(BigInteger.ONE);

				if(millerRabin(a,securityParameter)){
					return a;
				}
		}
	}

	//Finds multiplicative inverse ,i.e e^{-1} mod n
	static BigInteger mulInv(BigInteger e, BigInteger n){
		BigInteger t= BigInteger.ONE;
		BigInteger ans= BigInteger.ZERO;

		BigInteger a = n;
		BigInteger q= e.divide(n);
		BigInteger r= e.mod(n);

		BigInteger temp;
		while(r.compareTo(BigInteger.ZERO)==1){
			temp= t.subtract(q.multiply(ans));
			t=ans;
			ans=temp;
			e=n;
			n=r;
			q=e.divide(n);
			r=e.mod(n);
		}
			ans=ans.mod(a);
			if(ans.compareTo(BigInteger.ZERO)==-1) ans =ans.add(a);
			return ans;
	}
	
	static BigInteger getPrimitiveRoot(BigInteger q, BigInteger p){
		Random randomGenerator = new Random();

		BigInteger g ;

		while(true){
			g = new BigInteger(bitCount,randomGenerator);
			//if g is 2<g<q and g^p mod q and g^2 mod q is not 1
			//this is because we know q=2p+1, so we just chech for two factors
			if((g.compareTo(BigInteger.ONE)==1 && g.compareTo(q)==-1)&&(!modExp(g,p,q).equals(BigInteger.ONE) && !modExp(g,two,q).equals(BigInteger.ONE))) return g;
		}
	}

	static void keyGenerate(){

		while(true){
			p=generatePrime();
			q= p.multiply(two);
			q= q.add(BigInteger.ONE);
			if(q.compareTo(input)==1){
				if(millerRabin(q,3)) break;
			}
		}

		//System.out.println("q is "+q.longValue());
		//System.out.println("p is "+p.longValue());

		g = getPrimitiveRoot(q,p);
		//System.out.println("g is "+g.longValue());
		g=g.pow(2);


		while(true){
			a=new BigInteger(bitCount,new Random());

			if(a.compareTo(two)==1 && a.compareTo(q.subtract(two))==-1) break;
		}

		//System.out.println("a is "+a.longValue());

		 h = modExp(g,a,q);
		//PK q,g,h
		//SK a

	}

	static void encrypt( BigInteger m){
		BigInteger r;

		while(true){
			r= new BigInteger(bitCount,new Random());
			if( r.compareTo(two)==1 && r.compareTo(q.subtract(BigInteger.ONE))<1) break;
		}

		c1=modExp(g,r,q);
		c2= modExp(h,r,q).multiply(m.mod(q));
		//System.out.println("c1 is "+c1.longValue());
		//System.out.println("c2 is "+c2.longValue());
	}

	static BigInteger decrypt(BigInteger c1, BigInteger c2){
		BigInteger t1= modExp(c1,a,q);
		BigInteger t2= (c2.mod(q).multiply(mulInv(t1,q))).mod(q);
		return t2;
	} 

	public static void main(String[]args){
		//Put integer value to encrypt here
		input = BigInteger.valueOf(8);

		System.out.println("To encrypt " +input);
		keyGenerate();
		encrypt(input);
		System.out.println("Ciphertext C1 " +c1);
		System.out.println("Ciphertext C2 " +c2);

		BigInteger output=decrypt(c1,c2);
		System.out.println("decrypted "+output.longValue());
	}
}
