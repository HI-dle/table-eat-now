INSERT INTO public.p_reservation (guest_count, total_amount, created_at, created_by, deleted_at, deleted_by, id, reserver_id, updated_at, updated_by, reservation_name, reservation_uuid, reserver_contact, reserver_name, restaurant_timeslot_uuid, restaurant_uuid, cancel_reason, special_request, status, restaurant_details, restaurant_menu_details, restaurant_timeslot_details)
VALUES (2, 10000.00, '2025-04-20 17:59:37.825156', 1, null, null, 1, 1, '2025-06-20 17:59:38.567346', 1, '맛있는 식당(비빔밥 1건)', '00000000-0000-0000-0000-000000000001', '010-0000-0000', '홍길동', '840e02e0-e00d-431b-a650-8a3a1d8a1abb', '748f212b-10a4-4539-b6f5-b23dd8b83d06', null, '창가 자리', 'PENDING_PAYMENT',
        '{"name": "맛있는 식당", "address": "서울시 강남구", "ownerId": null, "staffId": null, "closingTime": [21, 0], "openingTime": [9, 0], "contactNumber": "02-000-0000"}',
        '{"name": "비빔밥", "price": 10000, "quantity": 1}',
        '{"timeslot": [12, 0], "availableDate": [2025, 4, 20]}');


INSERT INTO public.p_reservation_payment_detail (amount, created_at, created_by, deleted_at, deleted_by, id, reservation_id, updated_at, updated_by, detail_reference_id, reservation_payment_detail_uuid, type) VALUES (3000.00, '2025-04-20 20:53:47.656601', null, null, null, 1, 1, '2025-04-20 20:53:47.656601', null, '56f83847-0c9c-4957-88ee-2510e75fcf0d', '8c05afb4-6b3e-419f-8ea7-db4dd2765e38', 'PROMOTION_COUPON');
INSERT INTO public.p_reservation_payment_detail (amount, created_at, created_by, deleted_at, deleted_by, id, reservation_id, updated_at, updated_by, detail_reference_id, reservation_payment_detail_uuid, type) VALUES (7000.00, '2025-04-20 20:53:47.680114', null, null, null, 2, 1, '2025-04-20 20:53:47.680114', null, '5e2a9d2e-e440-41b7-9ed7-ffb9c9c485b6', '8d764197-d461-4a67-b511-dda9898197b5', 'PAYMENT');
