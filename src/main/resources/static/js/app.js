/**
 * app.js — Main entry point, wires all modules together
 * Author: Satyajeet Sharma
 */

import { getToken, clearAuth, switchTab, login, register, forgotStep1, forgotStep2, showForgotStep1 } from './auth.js';
import { loadTasks, filterTasks, createTask, updateStatus, deleteTask, openModal, closeModal } from './tasks.js';

// ─── INIT ────────────────────────────────────────────
window.addEventListener('DOMContentLoaded', () => {
  if (getToken()) {
    showApp();
  }

  // Close modal when clicking outside
  document.getElementById('modal').addEventListener('click', e => {
    if (e.target === e.currentTarget) closeModal();
  });
});

// ─── SHOW APP SCREEN ────────────────────────────────
export function showApp() {
  document.getElementById('auth-screen').style.display = 'none';
  document.getElementById('app-screen').style.display  = 'block';
  document.getElementById('user-name-display').textContent = localStorage.getItem('userName');
  loadTasks();
}

// ─── LOGOUT ─────────────────────────────────────────
function logout() {
  clearAuth();
  document.getElementById('app-screen').style.display  = 'none';
  document.getElementById('auth-screen').style.display = 'flex';
}

// ─── EXPOSE TO HTML (onclick handlers) ──────────────
// Auth
window.switchTab      = switchTab;
window.login          = login;
window.register       = register;
window.forgotStep1    = forgotStep1;
window.forgotStep2    = forgotStep2;
window.showForgotStep1 = showForgotStep1;
window.logout         = logout;

// Tasks
window.filterTasks  = filterTasks;
window.createTask   = createTask;
window.updateStatus = updateStatus;
window.deleteTask   = deleteTask;
window.openModal    = openModal;
window.closeModal   = closeModal;
