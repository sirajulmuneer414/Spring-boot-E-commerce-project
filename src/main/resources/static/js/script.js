const paymentStart = (e) => {
    e.preventDefault();
    console.log("payment started....")

    var radio = document.getElementsByName('paymentType');

    var paymentType;



    for (i = 0; i < radio.length; i++) {
        if (radio[i].checked) {
            paymentType = radio[i].value;
        }
    }

    console.log(paymentType);
    var csrfToken = $("meta[name='_csrf']").attr("content");

    if (paymentType == 'UPI PAYMENT') {
        $.ajax(
            {
                url: '/user/payment/razor',
                type: 'POST',
                dataType: 'json',
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('X-CSRF-TOKEN', csrfToken);
                },
                success: function (response) {
                    console.log(response);
                    console.log(response.amount_due);
                    let options = {
                        key: "rzp_test_3BnqGyxnP22wsH",
                        amount: response.amount,
                        currency: "INR",
                        name: "Lenscraft",
                        image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS8OJ9YzetXusA4UWJslRsey44iPf9Rq2SRhA&usqp=CAU",
                        order_id: response.id,
                        handler: function (response) {
                            console.log(response.razorpay_payment_id);
                            console.log(response.razorpay_order_id);
                            console.log(response.razorpay_signature);
                            console.log("payment successful");

                            Swal.fire({
                                title: "Successful!",
                                text: "Your payment is successful!",
                                icon: "success"
                            });
                            var formData = document.getElementById("orderForm");
                            formData.submit();

                        },
                        "prefill": {
                            "name": "",
                            "email": "",
                            "contact": ""
                        }, "notes": {
                            "address": "Razorpay Corporate Office"
                        },
                        "theme": {
                            "color": "#b43e8f"
                        }
                    };

                    let rzp = new Razorpay(options);

                    console.log(new Razorpay(options));

                    console.log(rzp);

                    rzp.on("payment.failed", function (response) {
                        Swal.fire({
                            icon: "error",
                            title: "Oops...",
                            text: "Something went wrong!"
                        });
                    });
                    rzp.open();
                }

            }
        )
    }

    else if (paymentType == 'CASH ON DELIVERY') {
        var formData = document.getElementById("orderForm");
        orderForm.submit();
    }
}

