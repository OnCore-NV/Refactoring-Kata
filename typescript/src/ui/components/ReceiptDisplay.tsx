import React from 'react';

interface Props {
  receiptText: string;
}

const ReceiptDisplay: React.FC<Props> = ({ receiptText }) => {
  return (
    <div className="receipt-display">
      <h2>Receipt</h2>
      <pre className="receipt-content">{receiptText}</pre>
    </div>
  );
};

export default ReceiptDisplay;
