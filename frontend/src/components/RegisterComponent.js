import React, { useState } from "react";
import AuthService from "../services/AuthService";

function Register({ setIsLogin }) {
  const [names, setNames] = useState("");
  const [email, setEmail] = useState("");
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

  const onChangeNames = (e) => {
    const names = e.target.value;
    setNames(names);
  };

  const onChangeEmail = (e) => {
    const email = e.target.value;
    setEmail(email);
  };

  const handleRegister = (e) => {
    e.preventDefault();
    setMessage("");
    setLoading(true);
    AuthService.register(names, email, username, password).then(
      (res) => {
        if (res.status === 200) {
          localStorage.setItem("token", res.data.token);
          localStorage.setItem("user", JSON.stringify(res.data.user));
          window.location.reload();
        } else {
          window.alert(res.message || "Login Failed");
        }
        setLoading(false);
      },
      (error) => {
        const resMessage =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();
        setLoading(false);
        setMessage(resMessage);
      }
    );
  };

  return (
    <div className="col-md-12">
      <div className="card card-container mw-450 mx-auto p-4">
        <h3 className="mb-4">Register</h3>
        {message && (
          <div className="form-group">
            <div className="alert alert-danger" role="alert">
              {message}
            </div>
          </div>
        )}
        <form onSubmit={handleRegister}>
          <div className="form-group">
            <label htmlFor="username">Names</label>
            <input
              type="text"
              className="form-control"
              name="names"
              value={names}
              onChange={onChangeNames}
            />
          </div>
          <div className="form-group">
            <label htmlFor="username">Email</label>
            <input
              type="text"
              className="form-control"
              name="email"
              value={email}
              onChange={onChangeEmail}
            />
          </div>
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
              <span>Register</span>
            </button>
          </div>
          <div className="form-group mt-4 mb-0 text-center">
            <span> Already have an account? </span>
            <a href="#" onClick={() => setIsLogin(true)}>
              Login
            </a>
          </div>
        </form>
      </div>
    </div>
  );
}

export default Register;
