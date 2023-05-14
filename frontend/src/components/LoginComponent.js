import React, { useState } from "react";
import AuthService from "../services/AuthService";

function Login({ setIsLogin }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const onChangeUsername = (e) => {
    const username = e.target.value;
    setUsername(username);
  };

  const onChangePassword = (e) => {
    const password = e.target.value;
    setPassword(password);
  };

  const handleLogin = (e) => {
    e.preventDefault();
    setMessage("");
    setLoading(true);
    AuthService.login(username, password)
      .then((res) => {
        if (res.status === 200) {
          localStorage.setItem("token", res.data.token);
          localStorage.setItem("user", JSON.stringify(res.data.user));
          window.location.reload();
        } else {
          window.alert(res.message || "Login Failed");
        }
        setLoading(false);
      })
      .catch((err) => {
        console.log(err);
        const resMessage =
          (err.response && err.response.data && err.response.data.message) ||
          err.message ||
          err.toString();
        setLoading(false);
        setMessage(resMessage);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <div className="col-md-12">
      <div className="card card-container mw-450 mx-auto p-4">
        <h3 className="mb-4">Login</h3>
        {message && (
          <div className="form-group mb-0">
            <div className="alert alert-danger" role="alert">
              {message}
            </div>
          </div>
        )}
        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              className="form-control"
              name="username"
              value={username}
              onChange={onChangeUsername}
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              className="form-control"
              name="password"
              value={password}
              onChange={onChangePassword}
            />
          </div>
          <div className="form-group">
            <button className="btn btn-primary btn-block" disabled={loading}>
              {loading && (
                <span className="spinner-border spinner-border-sm"></span>
              )}
              <span>Login</span>
            </button>
          </div>
          <div className="form-group mt-4 mb-0 text-center">
            <span> Don't have an account? </span>{" "}
            <a href="#" onClick={() => setIsLogin(false)}>
              Register
            </a>
          </div>
        </form>
      </div>
    </div>
  );
}

export default Login;
