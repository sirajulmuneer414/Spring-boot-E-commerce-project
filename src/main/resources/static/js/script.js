const paymentStart = (e) => {
    e.preventDefault();
    console.log("payment started....");

    var radio = document.getElementsByName('paymentType');
    var paymentType;

    for (i = 0; i < radio.length; i++) {
        if (radio[i].checked) {
            paymentType = radio[i].value;
        }
    }

    var csrfToken = $("meta[name='_csrf']").attr("content");
    var useWallet = document.getElementById('useWallet') ? document.getElementById('useWallet').checked : false;

    // Handle Full Wallet Payment (Radios disabled, wallet covers full amount)
    if (!paymentType && useWallet) {
        paymentType = 'WALLET';
        var hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'paymentType';
        hiddenInput.value = 'WALLET';
        document.getElementById('orderForm').appendChild(hiddenInput);
    }

    // Handle Wallet + UPI payment (wallet checked but doesn't cover full amount)
    if (paymentType == 'UPI PAYMENT' && useWallet) {
        $.ajax({
            url: '/user/payment/razor',
            type: 'POST',
            data: { useWallet: true },
            dataType: 'json',
            beforeSend: function (xhr) {
                xhr.setRequestHeader('X-CSRF-TOKEN', csrfToken);
            },
            success: function (response) {
                console.log("WALLET_PLUS_UPI payment - Razorpay order created");
                console.log("Wallet deduction amount:", response.walletDeduction);
                
                let options = {
                    key: response.key,
                    amount: response.amount,
                    currency: "INR",
                    name: "Lenscraft",
                    image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS8OJ9YzetXusA4UWJslRsey44iPf9Rq2SRhA&usqp=CAU",
                    order_id: response.id,
                    handler: function (razorpayResponse) {
                        console.log("WALLET_PLUS_UPI - Razorpay payment success");
                        
                        // Send to wallet-plus-upi confirm endpoint
                        $.ajax({
                            url: '/user/payment/wallet-plus-upi/confirm',
                            type: 'POST',
                            data: {
                                razorpay_payment_id: razorpayResponse.razorpay_payment_id,
                                razorpay_order_id: razorpayResponse.razorpay_order_id,
                                razorpay_signature: razorpayResponse.razorpay_signature
                            },
                            beforeSend: function (xhr) {
                                xhr.setRequestHeader('X-CSRF-TOKEN', csrfToken);
                            },
                            success: function () {
                                // Redirect to confirmation page
                                window.location.href = '/user/cart/buy/confirm';
                            },
                            error: function () {
                                alert("Payment verification failed! Please contact support.");
                            }
                        });
                    },
                    "prefill": {
                        "name": "",
                        "email": "",
                        "contact": ""
                    },
                    "notes": {
                        "address": "Razorpay Corporate Office"
                    },
                    "theme": {
                        "color": "#b43e8f"
                    }
                };

                let rzp = new Razorpay(options);
                rzp.on("payment.failed", function (response) {
                    Swal.fire({
                        icon: "error",
                        title: "Oops...",
                        text: "Payment failed! Please try again."
                    });
                });
                rzp.open();
            },
            error: function () {
                alert("Failed to create order. Please try again.");
            }
        });
    }
    // Handle UPI-only payment (no wallet)
    else if (paymentType == 'UPI PAYMENT') {
        $.ajax({
            url: '/user/payment/razor',
            type: 'POST',
            data: { useWallet: false },
            dataType: 'json',
            beforeSend: function (xhr) {
                xhr.setRequestHeader('X-CSRF-TOKEN', csrfToken);
            },
            success: function (response) {
                console.log("UPI-only payment - Razorpay order created");
                
                let options = {
                    key: response.key,
                    amount: response.amount,
                    currency: "INR",
                    name: "Lenscraft",
                    image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS8OJ9YzetXusA4UWJslRsey44iPf9Rq2SRhA&usqp=CAU",
                    order_id: response.id,
                    handler: function (razorpayResponse) {
                        console.log("UPI payment success");
                        
                        // Send Razorpay payment details to backend
                        $.ajax({
                            url: '/user/cart/buy/confirm',
                            type: 'POST',
                            data: {
                                razorpay_payment_id: razorpayResponse.razorpay_payment_id,
                                razorpay_order_id: razorpayResponse.razorpay_order_id,
                                razorpay_signature: razorpayResponse.razorpay_signature,
                                paymentType: 'UPI PAYMENT'
                            },
                            beforeSend: function (xhr) {
                                xhr.setRequestHeader('X-CSRF-TOKEN', csrfToken);
                            },
                            success: function () {
                                window.location.href = '/user/cart/buy/confirm';
                            },
                            error: function () {
                                alert("Payment verification failed! Please contact support.");
                            }
                        });
                    },
                    "prefill": {
                        "name": "",
                        "email": "",
                        "contact": ""
                    },
                    "notes": {
                        "address": "Razorpay Corporate Office"
                    },
                    "theme": {
                        "color": "#b43e8f"
                    }
                };

                let rzp = new Razorpay(options);
                rzp.on("payment.failed", function (response) {
                    Swal.fire({
                        icon: "error",
                        title: "Oops...",
                        text: "Payment failed! Please try again."
                    });
                });
                rzp.open();
            },
            error: function () {
                alert("Failed to create order. Please try again.");
            }
        });
    }
    // Handle COD or full Wallet payment
    else if (paymentType == 'CASH ON DELIVERY' || paymentType == 'WALLET') {
        var formData = document.getElementById("orderForm");
        formData.submit();
    }
}
