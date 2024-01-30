$(document).ready(function () {

    var contentArea = document.getElementById('contentArea');

    const onProductOffers = () => {

        contentArea.innerHTML = `
        <div>
        <div>
        <div class="d-flex justify-content-end pt-5">
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addCouponModal">Add Offer</button>
</div>
<div class="container mt-5">
    <table class="table text-center custom-table">
        <thead class="thead table-dark">
        <tr>

            <th>Product Name</th>
            <th>Minimun Quantity</th>
            <th>Start Date</th>
            <th>Expiry Date</th>
            <th>Discount Percentage</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="coupon :${product}">

            <td th:text="${product.productName}"></td>
            <td th:text="${product.offer.minimumQuantity}"></td>
            <td th:text="${product.offer.startDate}"></td>
            <td th:text="${product.offer.endDate}"></td>
            <td th:text="${product.offer.offerPercentage}"></td>
        
            <th>
                    <span  data-bs-toggle="modal" th:data-bs-target="'#editOfferModal-' + ${product.productId}">
                        <i class="fa-solid fa-pen-to-square" ></i>
                    </span>
                <!-- <form th:action="@{/admin/offer/product/edit}" method="post" >
                    <input hidden  th:value="${product.productId}" name="couponId">
                    <input hidden name="couponName" th:value="${coupon.couponName}">
                    <button type="submit" > <i class="fa-solid fa-pen-to-square text-danger"></i></button>
                </form> -->
                <form th:action="@{/admin/offer/product/delete}" method="post" >
                    <input hidden th:value="${product.productId}" name="couponId">
                    <button type="submit" > <i class="fas fa-trash text-danger"></i></button>
                </form>

            </th>
        </tr>
        </tbody>
    </table>
</div>
</div>
        </div>
        
<div th:each="product : ${products}">
<!-- Modal for editing offer -->
<div class="modal fade" th:id="'editOfferModal-' + ${product.productId}" tabindex="-1" aria-labelledby="'editOfferModalLabel-' + ${coupon.couponId}" aria-hidden="true">
    <!-- Modal content -->
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" th:id="'editOfferModalLabel-' + ${product.productId}">Edit Offer for <span th:text="${coupon.couponName}"></span></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <!-- Form to edit offer -->
                <form th:action="@{/admin/offer/edit}" th:method="post"  onsubmit="validateEditCouponForm(event)" th:object="${offer}" id="couponFormEdit">
                    <input type="hidden" name="productId" th:value="${product.productId}"   />
                    <div class="form-group">
                        <label for="couponNameEdit">Offers Percentage</label>
                        <input type="number" class="form-control" id="couponNameEdit" th:field="*{percentage}" th:value="${product.offer.offerPercentage}" >
                    </div>
                    <div class="form-group">
                        <label for="couponCodeEdit">Minimum Quantity</label>
                        <input type="text" class="form-control" id="couponCodeEdit" th:field="*{quantity}" th:value="${product.offer.minimumQuantity}">
                    </div>
                    <div class="form-group">
                        <label for="startDateEdit">Start Date</label>
                        <input type="date" class="form-control" id="startDateEdit" th:field="*{startDate}"  th:value="${product.offer.startDate}">
                    </div>
                    <div class="form-group">
                        <label for="expiryDateEdit">Expiry Date</label>
                        <input type="date" class="form-control" id="expiryDateEdit" th:field="*{endDate}"  th:value="${product.offer.endDate}">
                    </div>
                    <div class="form-group">
                        <label for="minimumPurchaseAmountEdit">Enter Offer Description</label>
                        <input type="number" class="form-control" id="minimumPurchaseAmountEdit" th:field="*{offerDescription}" th:value="${product.offer.offerDescription}">
                    </div>

                    <button type="submit" class="btn btn-primary mt-5">Save</button>
                </form>
            </div>

        </div>
    </div>
</div>
</div>





        `;

    }
});