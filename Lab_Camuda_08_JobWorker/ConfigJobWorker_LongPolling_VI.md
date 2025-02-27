
# So sánh Streaming và Long Polling: Ưu điểm và Nhược điểm
| **Khía cạnh**           | **Streaming (stream-enabled: true)**                                                                                                                                               | **Long Polling (stream-enabled: false)**                                                                                              |
|-------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| **Độ trễ (Latency)**    | - **Độ trễ thấp:** Broker đẩy thông báo job ngay lập tức, cung cấp cập nhật theo thời gian thực.                                                                                 | - **Độ trễ cao hơn:** Phải chờ đến khi request hết thời gian timeout hoặc gửi request mới.                                             |
| **Chi phí (Overhead)**  | - **Chi phí thấp:** Giữ kết nối duy nhất liên tục, giảm chi phí tạo các request mới.                                                                                               | - **Chi phí cao hơn:** Tạo nhiều request định kỳ tăng tải cho client và broker.                                                      |
| **Kết nối (Connection)**| - **Yêu cầu kết nối ổn định:** Cần môi trường mạng ổn định để duy trì kết nối liên tục; mạng không ổn định có thể làm gián đoạn stream.                                               | - **Chịu lỗi tốt hơn:** Mỗi request độc lập, nếu một request thất bại không ảnh hưởng đến các request sau.                              |
| **Độ phức tạp (Complexity)** | - **Phức tạp hơn:** Quản lý kết nối lâu dài, bao gồm việc xử lý reconnection và xử lý lỗi liên quan đến stream.                                                                         | - **Đơn giản hơn:** Cài đặt dễ dàng vì mỗi request là độc lập; không cần quản lý reconnection, dễ xử lý lỗi từng request.                  |
| **Sử dụng tài nguyên (Resource Usage)** | - **Có thể tiêu thụ nhiều tài nguyên:** Nếu không được quản lý tốt, kết nối liên tục có thể tiêu thụ nhiều tài nguyên trên client và broker.                                        | - **Tài nguyên theo request:** Mỗi request ngắn hạn tiêu thụ ít tài nguyên, nhưng tổng số request định kỳ có thể làm tăng tải tổng thể. |

# I. Long Polling

File này tổng hợp các cấu hình cần thiết cho cơ chế **long polling** trong Zeebe Client. Nội dung bao gồm:

1. Các cấu hình cần thiết (với mô tả, ý nghĩa và ví dụ giá trị thay đổi)
2. Các cấu hình liên quan cho worker
3. Ví dụ cấu hình YAML đầy đủ

---

## 1. Các cấu hình cần thiết

### - **enabled**
- **Mô tả:**  
  Xác định xem job worker có được kích hoạt hay không. Nếu `enabled: true`, worker sẽ đăng ký và nhận job từ broker.
- **Giá trị mặc định / Ví dụ:**
  - Mặc định: Có thể là `false` trong một số cấu hình, nhưng ví dụ dưới đây sử dụng `true` để kích hoạt.
  - Thay đổi: Chuyển sang `false` nếu bạn muốn tạm thời vô hiệu worker.

---

### - **stream-enabled**
- **Mô tả:**  
  Xác định chế độ nhận job của client.
  - Nếu `stream-enabled: false`, client sử dụng cơ chế long polling: gửi các request riêng lẻ theo chu kỳ.
  - Nếu `stream-enabled: true`, client sẽ mở kết nối streaming liên tục để nhận job ngay khi có sẵn.
- **Giá trị ví dụ:**
  - Ví dụ cấu hình: `stream-enabled: false`
- **Thay đổi:**
  - Chuyển thành `true` nếu muốn sử dụng streaming.

---

### - **poll-interval**
- **Mô tả:**  
  Khoảng thời gian giữa các lần gửi yêu cầu polling đến broker. Sau khi một yêu cầu kết thúc (do hoàn thành hoặc timeout), client sẽ chờ theo khoảng thời gian này trước khi gửi yêu cầu mới.
- **Giá trị mặc định / Ví dụ:**
  - Ví dụ cấu hình: `poll-interval: PT5S` nghĩa là client sẽ gửi yêu cầu mỗi 5 giây.
  - Thay đổi: Nếu cần polling nhanh hơn, có thể sử dụng `PT3S`; nếu cần chậm hơn, có thể dùng `PT60S`.

---

### - **request-timeout**
- **Mô tả:**  
  Xác định thời gian tối đa mỗi yêu cầu polling sẽ chờ phản hồi từ broker. Nếu không nhận được phản hồi trong khoảng thời gian này, yêu cầu sẽ bị timeout và bị hủy bỏ.
- **Giá trị mặc định / Ví dụ:**
  - Ví dụ cấu hình: `request-timeout: 1000` (hoặc `1000ms`) có nghĩa là mỗi request sẽ chờ tối đa 1 giây.
  - Thay đổi: Tùy thuộc vào tốc độ phản hồi của hệ thống, bạn có thể điều chỉnh thành `500ms` nếu hệ thống nhanh, hoặc `1500ms`/`2000ms` nếu cần thêm thời gian chờ.
  - Bạn có thể sử dụng cấu hình thời gian theo chuẩn: ISO-8601 (ví dụ: 10s)

---

## 2. Cấu hình liên quan cho Worker

### - **max-jobs-active**
- **Mô tả:**  
  Xác định số lượng job tối đa được kích hoạt đồng thời mà worker có thể xử lý.
- **Giá trị mặc định / Ví dụ:**
  - Ví dụ cấu hình: `max-jobs-active: 3`
  - Thay đổi: Tăng lên (ví dụ `5` hoặc `10`) nếu hệ thống của bạn có đủ tài nguyên và cần xử lý nhiều job đồng thời; giảm xuống nếu bạn muốn giới hạn tải.

---

### - **timeout**
- **Mô tả:**  
  Thời gian tối đa mà worker được phép xử lý một job và gửi phản hồi (hoặc hoàn thành job) cho broker. Nếu job không được hoàn thành trong thời gian này, nó sẽ bị timeout.
- **Giá trị mặc định / Ví dụ:**
  - Ví dụ cấu hình: `timeout: 1000` (1000ms, tức 1 giây).
  - Thay đổi: Nếu job cần xử lý phức tạp hơn, bạn có thể tăng thành `2000` (2 giây) hoặc `5000` (5 giây).
  - Bạn có thể sử dụng cấu hình thời gian theo chuẩn: ISO-8601 (ví dụ: 10s)

---

### - **auto-complete**
- **Mô tả:**  
  Xác định việc tự động hoàn thành job sau khi xử lý.
  - Nếu `auto-complete: true`, job sẽ được tự động đánh dấu hoàn thành nếu không có exception xảy ra.
  - Nếu `auto-complete: false`, bạn cần gọi lệnh `complete` hoặc `fail` thủ công, cho phép xử lý lỗi, retry, hoặc ghi log.
- **Giá trị mặc định / Ví dụ:**
  - Ví dụ cấu hình: `auto-complete: false`
  - Thay đổi: Có thể chuyển sang `true` nếu bạn muốn đơn giản hóa quá trình xử lý khi không có lỗi.

---

## 3. Ví Dụ Cấu Hình YAML mặc định cho job worker

Dưới đây là ví dụ file cấu hình liên quan đến long polling của Camunda 8:

```yaml
camunda:
  client:
    zeebe:
      defaults:
        enabled: true                   # Kích hoạt job worker
        auto-complete: false            # Tắt tự động hoàn thành; worker cần gọi complete sau khi xử lý
        poll-interval: PT5S             # Poll mỗi 5 giây
        request-timeout: 1000           # Timeout cho mỗi request là 1000ms (1 giây) hoặc 1s
        max-jobs-active: 3              # Worker sẽ xử lý tối đa 3 job đồng thời
        timeout: 1000                   # Timeout cho job lock là 1000ms (1 giây)
        fetch-variables: ''             # Danh sách các biến cần lấy; để trống nếu không cần
        force-fetch-all-variables: false  # Không ép fetch tất cả các biến
        name: ''                        # Tên của job worker (sử dụng mặc định nếu để trống)
        tenant-ids: ''                  # Tenant IDs, để trống nếu không dùng multi-tenancy
        type: ''                        # Loại job mà worker sẽ xử lý
      execution-threads: 1              # Số luồng xử lý đồng thời của worker
      prefer-rest-over-grpc: false      # Ưu tiên sử dụng gRPC
