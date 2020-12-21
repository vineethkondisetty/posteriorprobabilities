import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;

public class compute_a_posteriori
{
	private static void calculate_probability(double[] P_HCC, double[] P_H_CC, double P_CC, double P_H_LC, String observation_seq)
	{
		
		if (observation_seq != null && observation_seq.isEmpty())
			return;
		
		String sequence = observation_seq.substring(0, 1);
		double [] P_HCC_next = new double[P_HCC.length];
		print_result("\n After Observation:- " + observation_seq.charAt(0) + " = " + observation_seq.substring(1, observation_seq.length()) + "\n\n");
		
		for (int k = 0; k < P_HCC.length; k++)
		{
			
			P_HCC_next[k] = ((sequence.equals("C") ? P_H_CC[k] : ( 1 - P_H_CC[k] )) * P_HCC[k]) / (sequence.equals("C") ? P_CC : P_H_LC);
			
			print_result("P(h" + (k + 1) + " | Q) = " + deprecatedround_off(P_HCC_next[k], 5) + "\n");
		}
		
		double P_CC_next = 0, P_LC_next = 0;
		for (int i = 0; i < P_HCC.length; i++) 
		{
			
			P_CC_next += P_H_CC[i] * P_HCC_next[i];
			P_LC_next += (1 - P_H_CC[i]) * P_HCC_next[i];
		}
		print_result("\n Probability of picking next candy L, given Q: " + deprecatedround_off(P_LC_next, 5) + "\n\n");
		print_result("\n Probability of picking next candy C, given Q: " + deprecatedround_off(P_CC_next, 5));

		calculate_probability(P_HCC_next, P_H_CC, P_CC_next, P_LC_next, observation_seq.substring(1, observation_seq.length()));
	}

	private static String deprecatedround_off(double no, int precision)
	{

		int precision_scale = 5;

		return BigDecimal.valueOf(no).setScale(precision_scale, BigDecimal.ROUND_HALF_UP).toString();

	}
	
	private static void print_result(String seq_stmt) 
	{
		BufferedWriter bufferoutput = null;
		PrintWriter printoutput = null;
		FileWriter fileoutput = null;

		
		try 
		{

			fileoutput = new FileWriter("result.txt", true);
			bufferoutput = new BufferedWriter(fileoutput);
			printoutput = new PrintWriter(bufferoutput);
			printoutput.write(seq_stmt);
						
		} catch (Exception e) 
		{
			System.out.println(e);
		} 
		finally 
		{
			
			try {
				
				if (printoutput != null)
					printoutput.close();
				
			} 
			catch (Exception e1)
			{
				System.out.println(e1);
			}
		}
		
	}
	

	public static void main(String[] args)
	{
		double [] P_Hypothesis = {0.1, 0.2, 0.4, 0.2, 0.1};

		double [] P_H_CC = {1, 0.75, 0.5, 0.25, 0};

		String observation = "";
		if (args.length > 0)
			observation = args[0];

		print_result(" Observation sequence Q: " + observation);
		print_result("\n Length of Sequence: " + observation.length() + "\n");

		double P_CC = 0;
		for (int k = 0; k < P_Hypothesis.length; k++)
		{
			P_CC += ( P_H_CC[k] * P_Hypothesis[k] );
		}

		if (args.length < 1)
		{

			for (int i = 0; i < P_Hypothesis.length; i++)
			{
				print_result("P(h" + (i + 1) + " | Q) = " +deprecatedround_off(P_Hypothesis[i], 5) + "\n");
			}

			print_result("\n Probability that the next candy we pick will be C, given Q: " + deprecatedround_off(P_CC, 5));
			print_result("\n Probability that the next candy we pick will be L, given Q: " + deprecatedround_off(1 - P_CC, 5) + "\n\n");

			return;
		}

		calculate_probability(P_Hypothesis, P_H_CC, P_CC, (1 - P_CC), observation);
	}
	
}