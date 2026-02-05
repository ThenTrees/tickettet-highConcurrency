import http from 'k6/http';
import { check, sleep } from 'k6';

// Cấu hình kịch bản test
export const options = {
    stages: [
        { duration: '30s', target: 2000 },  // Tăng dần từ 0 lên 2000 users trong 30 giây đầu
        { duration: '1m', target: 10000 },  // Tiếp tục tăng lên 10000 users trong 1 phút
        { duration: '2m', target: 10000 },  // Giữ mức 10000 users trong 2 phút (đây là lúc test thực sự)
        { duration: '30s', target: 0 },      // Hạ tải dần về 0
    ],
    thresholds: {
        http_req_duration: ['p(95)<50'], // 95% số request phải nhanh hơn 50ms (vì bạn đang đạt 4ms nên mức này là an toàn)
        http_req_failed: ['rate<0.01'],  // Tỉ lệ lỗi phải thấp hơn 1%
    },
};

export default function () {
    // Thay đổi URL và ID cho đúng với API của bạn
    const ticketId = 1;
    const url = `http://localhost:1122/ticket/1/detail/1`;

    const res = http.get(url);

    // Kiểm tra xem phản hồi có đúng là 200 OK không
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 20ms': (r) => r.timings.duration < 20,
    });

    // Nghỉ 1 giây giữa các lần gọi để giả lập hành vi người dùng (tránh làm sập máy quá nhanh)
    sleep(1);
}