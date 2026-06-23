/**
 * auth.js — Handles login, register, and forgot password
 * Author: Satyajeet Sharma
 */

import { API } from './config.js';
import { showApp } from './app.js';

// ─── HELPERS ───────────────────────────────────────
export function saveAuth(token, name) {
  localStorage.setItem('token', token);
  localStorage.setItem('userName', name);
}

export function getToken() {
  return localStorage.getItem('token');
}

export function clearAuth() {
  localStorage.removeItem('token');
  localStorage.removeItem('userName');
}

function showError(msg) {
  document.getElementById('auth-error').textContent = msg;
  document.getElementById('auth-success').textContent = '';
}

function showSuccess(msg) {
  document.getElementById('auth-success').textContent = msg;
  document.getElementById('auth-error').textContent = '';
}

// ─── TAB SWITCHING ─────────────────────────────────
export function switchTab(tab, btn) {
  showError('');
  showSuccess('');
  document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
  if (btn) btn.classList.add('active');
  document.querySelectorAll('.step').forEach(s => s.classList.remove('active'));
  const map = { login: 'login-form', register: 'register-form', forgot: 'forgot-form' };
  document.getElementById(map[tab]).classList.add('active');
}

// ─── LOGIN ─────────────────────────────────────────
export async function login() {
  const email    = document.getElementById('login-email').value.trim();
  const password = document.getElementById('login-pass').value;
  if (!email || !password) return showError('Please fill all fields');
  try {
    const res  = await fetch(`${API}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    const data = await res.json();
    if (!data.success) return showError(data.message);
    saveAuth(data.data.token, data.data.name);
    showApp();
  } catch {
    showError('Cannot connect to server.');
  }
}

// ─── REGISTER ──────────────────────────────────────
export async function register() {
  const name             = document.getElementById('reg-name').value.trim();
  const email            = document.getElementById('reg-email').value.trim();
  const password         = document.getElementById('reg-pass').value;
  const securityQuestion = document.getElementById('reg-question').value;
  const securityAnswer   = document.getElementById('reg-answer').value.trim();

  if (!name || !email || !password)  return showError('Please fill all fields');
  if (!securityQuestion)             return showError('Please select a security question');
  if (!securityAnswer)               return showError('Please provide an answer');

  try {
    const res  = await fetch(`${API}/api/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, email, password, securityQuestion, securityAnswer })
    });
    const data = await res.json();
    if (!data.success) return showError(data.message);
    saveAuth(data.data.token, data.data.name);
    showApp();
  } catch {
    showError('Cannot connect to server.');
  }
}

// ─── FORGOT PASSWORD — STEP 1 ──────────────────────
export async function forgotStep1() {
  const email = document.getElementById('fp-email').value.trim();
  if (!email) return showError('Please enter your email');
  try {
    const res  = await fetch(`${API}/api/auth/forgot-password/question`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email })
    });
    const data = await res.json();
    if (!data.success) return showError(data.message);
    document.getElementById('question-display').textContent = '❓ ' + data.data.securityQuestion;
    showError('');
    document.querySelectorAll('.step').forEach(s => s.classList.remove('active'));
    document.getElementById('forgot-step2').classList.add('active');
  } catch {
    showError('Cannot connect to server.');
  }
}

// ─── FORGOT PASSWORD — STEP 2 ──────────────────────
export async function forgotStep2() {
  const email           = document.getElementById('fp-email').value.trim();
  const securityAnswer  = document.getElementById('fp-answer').value.trim();
  const newPassword     = document.getElementById('fp-newpass').value;
  const confirmPassword = document.getElementById('fp-confirmpass').value;

  if (!securityAnswer || !newPassword || !confirmPassword) return showError('Please fill all fields');
  if (newPassword !== confirmPassword) return showError('Passwords do not match');
  if (newPassword.length < 6)          return showError('Password must be at least 6 characters');

  try {
    const res  = await fetch(`${API}/api/auth/forgot-password/reset`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, securityAnswer, newPassword })
    });
    const data = await res.json();
    if (!data.success) return showError(data.message);
    showSuccess('✅ Password reset! Redirecting to login...');
    setTimeout(() => switchTab('login', document.querySelector('.tab-btn')), 2000);
  } catch {
    showError('Cannot connect to server.');
  }
}

// ─── BACK TO STEP 1 ────────────────────────────────
export function showForgotStep1() {
  showError('');
  document.querySelectorAll('.step').forEach(s => s.classList.remove('active'));
  document.getElementById('forgot-form').classList.add('active');
}
