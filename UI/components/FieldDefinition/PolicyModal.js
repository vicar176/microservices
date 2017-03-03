import React, { Component } from 'react';
import { Modal, Button } from 'react-bootstrap';
import styles from './index.scss';

export default function PolicyModal ({ activeModal, onClose }) {
  return (
    <Modal show={activeModal === 'policy-modal'} onHide={onClose} dialogClassName="modal-dialog">
      <Modal.Header closeButton>
        <Modal.Title>Per the Identity Theft Policy, PII is defined as the following</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <table className={"table table-bordered table-striped " + styles.policyModalTable}>
          <thead>
            <tr>
              <th>Category</th>
              <th>Data Element</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td rowSpan="10"><strong>Consumer Data</strong></td>
              <td>First, middle, or last name</td>
            </tr>
            <tr>
              <td>Date of birth</td>
            </tr>
            <tr>
              <td>Address</td>
            </tr>
            <tr>
              <td>E-mail</td>
            </tr>
            <tr>
              <td>Telephone or wireless numbers</td>
            </tr>
            <tr>
              <td>Social Security Number (Full)</td>
            </tr>
            <tr>
              <td>Social Security Number (Last 4 digits only)</td>
            </tr>
            <tr>
              <td>Government-issued identification number</td>
            </tr>
            <tr>
              <td>Maiden name</td>
            </tr>
            <tr>
              <td>MCM Account number</td>
            </tr>
            <tr>
              <td rowSpan="8"><strong>Financial Data</strong></td>
              <td>Original issuer card/account number (Charged off number)</td>
            </tr>
            <tr>
              <td>Credit card number (full active PAN)</td>
            </tr>
            <tr>
              <td>Credit card number (last 6 digits only)</td>
            </tr>
            <tr>
              <td>Credit card expiration date</td>
            </tr>
            <tr>
              <td>Cardholder name (stored with CC number)</td>
            </tr>
            <tr>
              <td>Cardholder address (stored with CC number)</td>
            </tr>
            <tr>
              <td>Bank Account Number</td>
            </tr>
            <tr>
              <td>Bank Routing Number</td>
            </tr>
            <tr>
              <td><strong>Medical Data</strong></td>
              <td>Doctor names and claims</td>
            </tr>
          </tbody>
        </table>
      </Modal.Body>
      <Modal.Footer>
        <Button onClick={onClose}>Close</Button>
      </Modal.Footer>
    </Modal>
  )
}
