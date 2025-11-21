;;;; shopping-cart.lisp

(in-package :supermarket-receipt)

;;; Constants for magic numbers
(defconstant +default-item-quantity+ 1.0)
(defconstant +two-for-amount-quantity+ 2)
(defconstant +three-for-two-quantity+ 3)
(defconstant +five-for-amount-quantity+ 5)
(defconstant +percentage-divisor+ 100.0)

;;; Constants for discount description strings
(defconstant +three-for-two-description+ "3 for 2")

(defclass shopping-cart ()
        ((items :initform nil
                :type list
                :accessor shopping-cart-items)
         (product-quantities :initform nil
                    :type list
                    :accessor shopping-cart-product-quantities)))

(defmethod add-item ((a-cart shopping-cart) (an-item product))
    (add-item-quantity a-cart an-item +default-item-quantity+))

(defmethod add-item-quantity ((a-cart shopping-cart) (an-item product) (a-quantity single-float))
    (push (make-instance 'product-quantity
              :product an-item
              :quantity a-quantity)
          (shopping-cart-items a-cart))
    (if (assoc an-item (shopping-cart-product-quantities a-cart))
        (incf (cdr (assoc an-item (shopping-cart-product-quantities a-cart))) a-quantity)
        (push (cons an-item a-quantity) (shopping-cart-product-quantities a-cart))))

(defmethod handle-offers ((a-cart shopping-cart) (a-receipt receipt) (offers list) (a-catalog supermarket-catalog))
  (let* ((product-quantities (shopping-cart-product-quantities a-cart))
         (products (mapcar #'car product-quantities)))
    (mapcar (lambda (a-product)
              (let ((a-quantity (cdr (assoc a-product product-quantities)))
                    (offer-for-product (cdr (assoc a-product offers))))
                (when offer-for-product
                  (let ((a-unit-price (unit-price a-catalog a-product))
                        (floored-quantity (floor a-quantity))
                        (a-discount nil)
                        (x 1)
                        (the-offer-type (offer-type offer-for-product)))
                    (if (eq the-offer-type 'three-for-two)
                        (setf x +three-for-two-quantity+)
                        (when (eq the-offer-type 'two-for-amount)
                          (setf x +two-for-amount-quantity+)
                          (when (>= floored-quantity +two-for-amount-quantity+)
                            (let* ((total (+ (* (offer-argument offer-for-product) (floor (/ floored-quantity x)))
                                             (* (mod floored-quantity +two-for-amount-quantity+) a-unit-price)))
                                   (discount-n (- (* a-unit-price a-quantity) total)))
                              (setf a-discount (make-instance 'discount 
                                                              :product a-product
                                                              :description (format nil "2 for ~S" (offer-argument offer-for-product))
                                                              :amount (- discount-n)))))))
                    (when (eq the-offer-type 'five-for-amount)
                      (setf x +five-for-amount-quantity+))
                    (let ((number-of-x (floor (/ floored-quantity x))))
                      (when (and (eq the-offer-type 'three-for-two)
                                 (> floored-quantity +two-for-amount-quantity+))
                        (let ((discount-amount (- (* a-quantity a-unit-price)
                                                  (+ (* number-of-x +two-for-amount-quantity+ a-unit-price)
                                                     (* (mod floored-quantity +three-for-two-quantity+) a-unit-price)))))
                          (setf a-discount (make-instance 'discount
                                                          :product a-product
                                                          :description +three-for-two-description+
                                                          :amount (- discount-amount)))))
                      (when (eq the-offer-type 'ten-percent-discount)
                        (setf a-discount (make-instance 'discount
                                                        :product a-product
                                                        :description (format nil "~S % off" (offer-argument offer-for-product))
                                                        :amount (/ (* (- a-quantity) a-unit-price (offer-argument offer-for-product)) +percentage-divisor+))))
                      (when (and (eq the-offer-type 'five-for-amount)
                                 (>= floored-quantity +five-for-amount-quantity+))
                        (let ((discount-total (- (* a-quantity a-unit-price)
                                                 (+ (* (offer-argument offer-for-product) number-of-x)
                                                    (* (mod floored-quantity +five-for-amount-quantity+) a-unit-price)))))
                          (setf a-discount (make-instance 'discount
                                                          :product a-product
                                                          :description (format nil "~S for ~S" x (offer-argument offer-for-product))
                                                          :amount (- discount-total)))))
                      (when a-discount
                        (add-discount a-receipt a-discount)))))))
            products)))
