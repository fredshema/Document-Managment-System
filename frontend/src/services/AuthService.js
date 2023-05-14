import http from "../Common";

class AuthService {
  login(username, password) {
    return http.post("/login", { username, password });
  }
  register(names, email, username, password) {
    return http.post("/register", { names, email, username, password });
  }
}

export default new AuthService();