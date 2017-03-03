import React, { Component } from 'react';
import { Modal, Button } from 'react-bootstrap';
import config from 'config';
import CssLoad from 'components/Common/cssLoad';
const { utilitiesService } = config;

export default function FailedDocumentModal ({ activeModal, failedPdf, documentId, onClose, bucketName, onFinishLoadingPdf, isPdfPresent}) {
  return (
    <Modal show={activeModal === 'failed-docs-modal'} onHide={onClose} dialogClassName="modal-dialog modal-lg">
      <Modal.Header closeButton>
        <Modal.Title>Document ID: {documentId}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <iframe style={{position: 'relative'}} src={`${utilitiesService}s3/${bucketName}/objects?key=${failedPdf}#page=1&zoom=100`} width="100%" height="450px" onLoad={onFinishLoadingPdf} id="documentImage" className={!isPdfPresent?"hide":""}/>
        <p className={isPdfPresent?"hide":"text-center"}>There was an error loading the template pdf.</p>
      </Modal.Body>
      <Modal.Footer>
        <Button onClick={onClose}>Close</Button>
      </Modal.Footer>
    </Modal>
  )
}
