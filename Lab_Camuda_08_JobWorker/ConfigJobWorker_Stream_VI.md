# Streaming vs Long Polling: Pros and Cons Comparison

| **Aspect**          | **Streaming (stream-enabled: true)**                                                                                                                                                    | **Long Polling (stream-enabled: false)**                                                                                                      |
|---------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| **Latency**         | - **Low latency:** Broker pushes job notifications immediately, providing near real-time updates.                                                                                       | - **Higher latency:** Must wait until the request times out or a new polling request is sent.                                                   |
| **Overhead**        | - **Lower overhead:** One persistent connection is maintained, reducing the cost of creating new requests.                                                                               | - **Higher overhead:** Frequent creation of new requests increases load on both client and broker.                                              |
| **Connection**      | - **Requires stable connection:** Needs a reliable and continuous network connection; unstable networks may disrupt the persistent stream.                                              | - **Resilient to network issues:** Each polling request is independent, so a failed request does not affect subsequent ones.                      |
| **Complexity**      | - **More complex:** Requires management of persistent connections, reconnection strategies, and error handling over a long-lived stream.                                               | - **Simpler setup:** Easier to implement since each request is isolated; simpler error handling and no need for reconnection management.         |
| **Resource Usage**  | - **Potential for higher resource consumption:** Persistent connections, if not managed well, might consume more resources on both the client and broker.                             | - **Generally lower per request:** Each request is short-lived, which can reduce the per-request resource usage, though overall overhead may increase. |

# I. Streaming

File này tổng hợp các cấu hình cần thiết để sử dụng chế độ **streaming** trong Zeebe Client. Khi sử dụng streaming, client mở một kết nối liên tục với broker để nhận các job ngay khi có sẵn, thay vì gửi các yêu cầu riêng lẻ như long polling. Nội dung bao gồm:

1. Các cấu hình cần thiết (với mô tả, ý nghĩa và ví dụ giá trị thay đổi)
2. Các cấu hình liên quan cho worker
3. Ví dụ cấu hình YAML đầy đủ cho chế độ streaming

---

## 1. Các cấu hình cần thiết

### - **enabled**
- **Mô tả:**  
  Xác định xem job worker có được kích hoạt hay không. Nếu `enabled: true`, worker sẽ đăng ký và nhận job từ broker.
- **Giá trị ví dụ:**
  - Ví dụ cấu hình: `enabled: true`
- **Thay đổi:**
  - Chuyển thành `false` nếu muốn tạm thời vô hiệu hóa worker.

---

### - **stream-enabled**
- **Mô tả:**  
  Xác định chế độ nhận job của client.
  - Nếu `stream-enabled: true`, client sẽ mở kết nối streaming liên tục với broker để nhận job ngay lập tức khi có sẵn.
  - Nếu `stream-enabled: false`, client sẽ sử dụng cơ chế long polling, gửi các request riêng lẻ theo chu kỳ.
- **Giá trị ví dụ:**
  - Ví dụ cấu hình: `stream-enabled: true`
- **Thay đổi:**
  - Chuyển thành `false` nếu bạn muốn sử dụng long polling thay thế.

---

### - **stream-timeout**
- **Mô tả:**  
  Xác định khoảng thời gian tối đa mà kết nối streaming được duy trì khi không có dữ liệu gửi qua.  
  Nếu không có thông báo nào được gửi trong khoảng thời gian này, kết nối có thể bị coi là đã hết hạn và được tái thiết lập.
- **Giá trị ví dụ:**
  - Ví dụ cấu hình: `stream-timeout: 3S` nghĩa là 3 giây.
- **Thay đổi:**
  - Bạn có thể tăng giá trị này nếu môi trường có lưu lượng dữ liệu không đều, hoặc giảm nếu muốn kết nối tái thiết lập nhanh hơn khi không có dữ liệu.

---

### - **request-timeout**
- **Mô tả:**  
  Xác định thời gian tối đa mà một yêu cầu (ví dụ: yêu cầu thiết lập streaming ban đầu) sẽ chờ phản hồi từ broker.
- **Giá trị ví dụ:**
  - Ví dụ cấu hình: `request-timeout: 1000` (1000ms, tức 1 giây).
- **Thay đổi:**
  - Có thể điều chỉnh xuống `500ms` nếu hệ thống phản hồi nhanh, hoặc tăng lên `1500ms`/`2000ms` nếu cần thêm thời gian chờ.

---

## 2. Cấu hình Liên quan cho Worker

### - **max-jobs-active**
- **Mô tả:**  
  Xác định số lượng job tối đa được kích hoạt đồng thời mà worker có thể xử lý.
- **Giá trị ví dụ:**
  - Ví dụ cấu hình: `max-jobs-active: 3`
- **Thay đổi:**
  - Tăng lên (ví dụ `5` hoặc `10`) nếu hệ thống có đủ tài nguyên, hoặc giảm nếu muốn giới hạn tải công việc.

---

### - **timeout**
- **Mô tả:**  
  Thời gian tối đa để worker xử lý một job và gửi phản hồi (hoặc hoàn thành job) cho broker.  
  Nếu job không được xử lý xong trong thời gian này, nó sẽ bị timeout.
- **Giá trị ví dụ:**
  - Ví dụ cấu hình: `timeout: 1000` (1000ms, tức 1 giây).
- **Thay đổi:**
  - Nếu job có tính chất xử lý phức tạp hơn, có thể tăng lên `2000` (2 giây) hoặc `5000` (5 giây).

---

### - **auto-complete**
- **Mô tả:**  
  Xác định việc tự động hoàn thành job sau khi xử lý.
  - Nếu `auto-complete: true`, job sẽ được tự động đánh dấu hoàn thành nếu không có exception xảy ra.
  - Nếu `auto-complete: false`, bạn cần gọi lệnh `complete` hoặc `fail` thủ công, cho phép bạn kiểm soát chi tiết quá trình xử lý lỗi và retry.
- **Giá trị ví dụ:**
  - Ví dụ cấu hình: `auto-complete: false`
- **Thay đổi:**
  - Có thể chuyển sang `true` nếu muốn tự động hoàn thành job khi không có lỗi xảy ra.

---

## 3. Ví Dụ Cấu Hình YAML Đầy Đủ Cho Streaming Mode

Dưới đây là ví dụ file cấu hình sử dụng chế độ streaming trong Camunda 8:

```yaml
camunda:
  client:
    zeebe:
      defaults:
        enabled: true                   # Kích hoạt job worker
        stream-enabled: true            # Bật chế độ streaming (kết nối liên tục)
        auto-complete: false            # Tắt tự động hoàn thành; worker cần gọi complete sau khi xử lý
        stream-timeout: 3S              # Kết nối streaming sẽ timeout nếu không có dữ liệu trong 3 giây
        request-timeout: 1000           # Timeout cho mỗi request (ví dụ: thiết lập streaming) là 1000ms (1 giây)
        max-jobs-active: 3              # Worker sẽ xử lý tối đa 3 job đồng thời
        timeout: 1000                   # Timeout cho job lock là 1000ms (1 giây)
        fetch-variables: ''             # Danh sách các biến cần lấy; để trống nếu không cần
        force-fetch-all-variables: false  # Không ép fetch tất cả các biến
        name: ''                        # Tên của job worker (sử dụng mặc định nếu để trống)
        tenant-ids: ''                  # Tenant IDs, để trống nếu không dùng multi-tenancy
        type: ''         # Loại job mà worker sẽ xử lý
      execution-threads: 1              # Số luồng xử lý đồng thời của worker
      prefer-rest-over-grpc: false      # Ưu tiên sử dụng gRPC
