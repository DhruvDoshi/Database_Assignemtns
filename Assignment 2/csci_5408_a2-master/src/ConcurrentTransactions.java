import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;

public class ConcurrentTransactions implements Runnable{
	private static final String db = "jdbc:mysql://104.154.60.193:3306/csci5408_a2";
	private static final String user = "vishal";
	private static final String password = "Vishal@123";
	
	private String city;
	private static Queue<Integer> q= developQueue();
	private int threadindex;
	
	
	public ConcurrentTransactions(String city,int threadindex)
	{
		this.city=city;
		this.threadindex=threadindex;
	}
	
	//building the sequence as provided in a queue(first in first out manner)
	private static Queue<Integer> developQueue()
	{
		Queue<Integer> q=new LinkedList<Integer>();
		q.add(1);//T1 read
		q.add(2);//T2 read
		q.add(1);//T1 write
		q.add(3);//T3 read
		q.add(2);//T2 write
		q.add(3);//T3 write
		q.add(1);//T1 commit
		q.add(3);//T3 commit
		q.add(2);//T2 commit
		return q;
		
	}
	
	private static String readcustomers()
	{
		return "select * from customer1 where customer_zip_code_prefix=1151";
		
	}
	private static String updateCity()
	{
		return "update customer1 set customer_city = ? where  customer_zip_code_prefix=1151";
		
	}
	private static String committrans()
	{
		return "commit";
	}
	public static void main(String args[])
	{
		
		ConcurrentTransactions ct1=new ConcurrentTransactions("T1 City", 1);
		ConcurrentTransactions ct2=new ConcurrentTransactions("T2 City", 2);
		ConcurrentTransactions ct3=new ConcurrentTransactions("T3 City", 3);
		Thread t1=new Thread(ct1);
		Thread t2=new Thread(ct2);
		Thread t3=new Thread(ct3);
		t1.start();
		t2.start();
		t3.start();    	
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("log.txt",true));
			Connection conn=DriverManager.getConnection(db,user,password);
			conn.setAutoCommit(false);
			int i=0;
			out.write("Threadstart"+threadindex+"\n");
			out.close();
			while(!q.isEmpty())
			{
				System.out.println(q);
				if(!q.contains(threadindex))
					break;
				if(q.peek()!=null && q.peek().equals(threadindex))
				{
					try{
						if(i==0){
							Statement stmt=conn.createStatement();
							ResultSet rs=stmt.executeQuery(readcustomers());
							out = new BufferedWriter(new FileWriter("log.txt",true));
							out.write("Read from thread"+threadindex+"\n");
							out.close();
							while(rs.next())
							{
								out = new BufferedWriter(new FileWriter("log.txt",true));
								out.write(rs.getString("customer_id")+"\t"+rs.getString("customer_unique_id")+"\t"+rs.getString("customer_zip_code_prefix")+"\t"+rs.getString("customer_city")+"\t"+rs.getString("customer_state")+"\n");
								out.close();
							}
							rs.close();
							stmt.close();
							q.remove();
						}
						else if(i==1){
							out = new BufferedWriter(new FileWriter("log.txt",true));
							out.write("Update from thread"+threadindex+"\n");
							out.close();
							PreparedStatement stmt1=conn.prepareStatement(updateCity());
							stmt1.setString(1, city);
							stmt1.executeUpdate();
							
							stmt1.close();
							q.remove();
							
						}
						else if(i==2){
							Statement stmt3=conn.createStatement();
							stmt3.executeUpdate(committrans());
							out = new BufferedWriter(new FileWriter("log.txt",true));
							out.write("Commit from thread"+threadindex+"\n");
							out.close();
							
							conn.commit();
							
							stmt3.close();
							conn.close();
							q.remove();		
							
						}
						else{
							
							return;
						}
						
					}
					catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					i++;
					
				}
			}
			out.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
}







