package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;


public class PieceViewActivity extends Activity {
    Piece currentPiece;
    EditText editTextPiece;

    private OkCancelDialog deletePieceDialog;
    private OkCancelDialogListener deletePieceDialogListener =
            new OkCancelDialogListener() {
                public void ok() {
                    deletePiece();
                    setResult(Activity.RESULT_OK, null);
                    finish();
                }

                public void cancel() {
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piece_view);
        editTextPiece = (EditText) findViewById(R.id.editTextPiece);
        deletePieceDialog = new OkCancelDialog(this);
        deletePieceDialog.setListener(deletePieceDialogListener);

        Bundle bundle = getIntent().getExtras();
        long pieceId = bundle.getLong("pieceid");
        currentPiece = new Piece(pieceId);
        editTextPiece.setText(currentPiece.getName());
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    public boolean validName(String name) {
        if (name == null) return false;
        if (name.length() < 3) return false;

        return true;
    }

    public void deletePiece() {
        Piece.delete(currentPiece);
    }

    public void backButtonHandler(View v) {
        setResult(Activity.RESULT_CANCELED, null);
        finish();
    }

    public void deleteButtonHandler(View v) {
        deletePieceDialog.open("Delete " + currentPiece.getName());
    }

    public void renameButtonHandler(View v) {
        String name = editTextPiece.getText().toString().trim();

        if (!validName(name)) {
            Toast.makeText(getApplicationContext(), "Invalid name", Toast.LENGTH_SHORT).show();
            return;
        }
        Piece piece = Piece.getPieceByNameAndComposerId(name, currentPiece.getComposerId());

        if ( piece != null)
            Toast.makeText(getApplicationContext(), "Already exists", Toast.LENGTH_SHORT).show();
        else {
            currentPiece.setName(name);
            Piece.update(currentPiece);
            setResult(Activity.RESULT_OK, null);
            finish();
        }
    }

    public void planButtonHandler(View v) {
        int priority = Plan.countPlans() + 1;
        Plan.add(currentPiece.getMyid(),"","",15,priority,"SMTWTF_");

        Intent intent = new Intent(PieceViewActivity.this, PlanListActivity.class);
        startActivityForResult(intent, 0);
/*
        Bundle bundle = new Bundle();
        bundle.putLong("pieceid", currentPiece.getMyid());
        Intent intent = new Intent(PieceViewActivity.this, PlanEditorActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
        */
    }

    public void practiceButtonHandler(View v) {
        Bundle bundle = new Bundle();
        bundle.putLong("pieceid", currentPiece.getMyid());
        Intent intent = new Intent(PieceViewActivity.this, PracticeSessionActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
    }

    public void recitalButtonHandler(View v) {
    }

    public void searchButtonHandler(View v) {
    }
}
