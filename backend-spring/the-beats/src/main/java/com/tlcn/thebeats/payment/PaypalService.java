package com.tlcn.thebeats.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Payee;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.tlcn.thebeats.models.CartItem;

@Service
public class PaypalService {
	
//	@Autowired
//	private APIContext apiContext;
	
	@Autowired
	private PaypalConfig paypalConfig;
	
	public Payment createPayment(String emailPayee,
			List<CartItem> items,
			Double total, 
			String currency, 
			String method,
			String intent,
			String description, 
			String cancelUrl, 
			String successUrl) throws PayPalRESTException{
		Amount amount = new Amount();
		amount.setCurrency(currency);
		total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
		amount.setTotal(String.format("%.2f", total));

		ItemList itemList = new ItemList();
		List<Item> list = new ArrayList<>();
		if(items!=null)
		{
			for (CartItem cartItem : items) {
				Item item = new Item();
				item.setCurrency("USD");
				item.setName(cartItem.getSongName());
				item.setPrice( cartItem.getPrice()+"");
				item.setQuantity("1");
				list.add(item);
			}
			itemList.setItems(list);
		}
		
		Transaction transaction = new Transaction();
		transaction.setDescription(description);
		transaction.setAmount(amount);
		transaction.setItemList(itemList);
		Payee payee= new Payee();
		payee.setEmail(emailPayee);
		//payee.setMerchantId("RFN4S7JEBBLNN");
		transaction.setPayee(payee);
		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction);
		
		Payer payer = new Payer();
		payer.setPaymentMethod(method.toString());

		Payment payment = new Payment();
		payment.setIntent(intent.toString());
		//payment.setPayee(payee);
		payment.setPayer(payer);  
		payment.setTransactions(transactions);
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(cancelUrl);
		redirectUrls.setReturnUrl(successUrl);
		payment.setRedirectUrls(redirectUrls);
		
		return payment.create(paypalConfig.apiContext());
	}
	
	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
		Payment payment = new Payment();
		payment.setId(paymentId);
		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);
		return payment.execute(paypalConfig.apiContext(), paymentExecute);
	}

	public Payment getPaymentDetails(String paymentId) throws PayPalRESTException {
		APIContext apiContext= paypalConfig.apiContext();
		return Payment.get(apiContext, paymentId);
	}

}
