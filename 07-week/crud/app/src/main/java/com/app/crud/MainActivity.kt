package com.app.crud

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.crud.ui.theme.CrudTheme
import com.google.firebase.database.*

// 1. DATA MODEL
// Simple model for our items. 'id' is used as the key in Firebase.
data class Item(
    val id: String = "",
    val text: String = ""
)

class MainActivity : ComponentActivity() {

    // 2. FIREBASE INITIALIZATION
    // Get reference to the 'items' node in the Realtime Database
    private val database = FirebaseDatabase.getInstance().getReference("items")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrudTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CrudScreen(
                        database = database,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CrudScreen(database: DatabaseReference, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    // State for the list of items
    var itemList by remember { mutableStateOf(listOf<Item>()) }
    
    // State for the input field
    var inputText by remember { mutableStateOf("") }
    
    // State for editing mode
    var editingItem by remember { mutableStateOf<Item?>(null) }

    // 3. READ: Listen in real-time and update UI automatically
    // We use LaunchedEffect to register the listener once
    LaunchedEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<Item>()
                for (data in snapshot.children) {
                    val item = data.getValue(Item::class.java)
                    if (item != null) {
                        items.add(item)
                    }
                }
                itemList = items
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        database.addValueEventListener(listener)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Firebase CRUD ", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(16.dp))

        // Input Field
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text(if (editingItem == null) "Nuevo Item" else "Editar Item") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // CREATE / UPDATE Button
        Button(
            onClick = {
                if (inputText.isNotBlank()) {
                    if (editingItem == null) {
                        // CREATE: Add item to Firebase
                        val id = database.push().key ?: ""
                        val newItem = Item(id, inputText)
                        database.child(id).setValue(newItem)
                        Toast.makeText(context, "Added successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        // UPDATE: Edit item and update Firebase
                        val updatedItem = editingItem!!.copy(text = inputText)
                        database.child(updatedItem.id).setValue(updatedItem)
                        editingItem = null
                        Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    inputText = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (editingItem == null) "Agregar Item" else "Actualizar Item")
        }

        if (editingItem != null) {
            TextButton(onClick = { 
                editingItem = null 
                inputText = ""
            }) {
                Text("Cancelar Edicion")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of Items
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(itemList) { item ->
                ItemRow(
                    item = item,
                    onEdit = {
                        editingItem = it
                        inputText = it.text
                    },
                    onDelete = {
                        // DELETE: Remove item from Firebase
                        database.child(it.id).removeValue()
                        Toast.makeText(context, "Eliminado exitosamente", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun ItemRow(item: Item, onEdit: (Item) -> Unit, onDelete: (Item) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = item.text, modifier = Modifier.weight(1f))
            
            IconButton(onClick = { onEdit(item) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            
            IconButton(onClick = { onDelete(item) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
